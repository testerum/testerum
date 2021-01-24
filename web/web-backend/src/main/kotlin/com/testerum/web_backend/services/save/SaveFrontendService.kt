package com.testerum.web_backend.services.save

import com.testerum.file_service.file.ResourceFileService
import com.testerum.file_service.util.isChangedRequiringSave
import com.testerum.file_service.util.isStepPatternChangeCompatible
import com.testerum.model.arg.Arg
import com.testerum.model.exception.ValidationException
import com.testerum.model.feature.Feature
import com.testerum.model.resources.ResourceContext
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureParserFactory
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.util.isOtherStepWithTheSameStepPatternAsTheNew
import com.testerum.web_backend.util.isStepPatternUsed

class SaveFrontendService(private val webProjectManager: WebProjectManager,
                          private val resourceFileService: ResourceFileService) {

    private fun stepsCache() = webProjectManager.getProjectServices().getStepsCache()

    private fun getResourcesDir() = webProjectManager.getProjectServices().dirs().getResourcesDir()

    fun saveFeature(feature: Feature, recursiveSave: Boolean = true): Feature {
        if (recursiveSave) {
            saveStepCallsRecursively(feature.hooks.beforeAll)
            saveStepCallsRecursively(feature.hooks.beforeEach)
            saveStepCallsRecursively(feature.hooks.afterEach)
            saveStepCallsRecursively(feature.hooks.afterAll)
        }

        val featureToSave = if (recursiveSave) {
            saveFeatureExternalResources(feature)
        } else {
            feature
        }

        val featuresDir = webProjectManager.getProjectServices().dirs().getFeaturesDir()

        val savedFeature = webProjectManager.getProjectServices().getFeatureCache().save(featureToSave, featuresDir)

        // if the feature was renamed, notify tests cache
        val oldPath = featureToSave.oldPath
        val newPath = savedFeature.path
        if (oldPath != null && newPath != oldPath) {
            webProjectManager.reloadProject()
        }

        if (featureToSave.hooks.hasHooks()) {
            reinitializeCaches()
        }

        return savedFeature
    }

    fun saveTest(test: TestModel): TestModel {
        val savedTest = saveTestWithoutReinitializingCaches(test)

        reinitializeCaches()

        return savedTest
    }

    private fun saveTestWithoutReinitializingCaches(test: TestModel, recursiveSave: Boolean = true): TestModel {
        if (recursiveSave) {
            saveStepCallsRecursively(test.stepCalls)
            saveStepCallsRecursively(test.afterHooks)
        }

        val testToSave = if (recursiveSave) {
            saveTestExternalResources(test)
        } else {
            test
        }

        return webProjectManager.getProjectServices().getTestsCache().save(testToSave)
    }

    fun saveComposedStep(composedStep: ComposedStepDef): ComposedStepDef {
        val savedStep = saveComposedStepWithoutReinitializingCaches(composedStep)

        reinitializeCaches()

        return savedStep
    }

    private fun saveComposedStepWithoutReinitializingCaches(composedStep: ComposedStepDef, recursiveSave: Boolean = true): ComposedStepDef {
        validateComposedStepSave(composedStep)

        if (recursiveSave) {
            saveStepCallsRecursively(composedStep.stepCalls)
        }

        val stepToSave: ComposedStepDef = if (recursiveSave) {
            saveExternalResources(composedStep)
        } else {
            composedStep
        }

        val existingStep = stepToSave.oldPath?.let { stepsCache().getComposedStepAtPath(it) }

        val savedComposedStep = stepsCache().saveComposedStep(stepToSave)

        // update affected tests & steps
        if (existingStep != null) {
            if (composedStep.isChangedRequiringSave(existingStep)) {
                val existingStepPattern = existingStep.stepPattern
                val newStepPattern = savedComposedStep.stepPattern

                if (existingStepPattern.isStepPatternChangeCompatible(newStepPattern)) {
                    updateFeaturesThatUseOldStep(existingStep, savedComposedStep)
                    updateTestsThatUseOldStep(existingStep, savedComposedStep)
                    updateStepsThatUsesOldStep(existingStep, savedComposedStep)
                }
            }
        }

        return savedComposedStep
    }

    private fun updateFeaturesThatUseOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef) {
        val featuresThatUsesOldStep = findFeaturesThatCall(oldStep.stepPattern)

        for (featureToUpdate in featuresThatUsesOldStep) {
            var updatedFeature = featureToUpdate

            for ((index, stepCall) in featureToUpdate.hooks.beforeAll.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedFeature = updateStepPatternForFeatureBeforeAllHooks(updatedFeature, index, newStep)
                }
            }

            for ((index, stepCall) in featureToUpdate.hooks.beforeEach.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedFeature = updateStepPatternForFeatureBeforeEachHooks(updatedFeature, index, newStep)
                }
            }
            for ((index, stepCall) in featureToUpdate.hooks.afterEach.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedFeature = updateStepPatternForFeatureAfterEachHooks(updatedFeature, index, newStep)
                }
            }

            for ((index, stepCall) in featureToUpdate.hooks.afterAll.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedFeature = updateStepPatternForFeatureAfterAllHooks(updatedFeature, index, newStep)
                }
            }

            saveFeature(updatedFeature, recursiveSave = false)
        }
    }

    private fun findFeaturesThatCall(searchedStepPattern: StepPattern): List<Feature> {
        val result: MutableList<Feature> = mutableListOf()

        val allFeatures = webProjectManager.getProjectServices().getFeatureCache().getAllFeatures()

        for (feature in allFeatures) {
            if (feature.hooks.beforeAll.isStepPatternUsed(searchedStepPattern)) {
                result.add(feature)
            } else
            if (feature.hooks.beforeEach.isStepPatternUsed(searchedStepPattern)) {
                result.add(feature)
            } else
            if (feature.hooks.afterEach.isStepPatternUsed(searchedStepPattern)) {
                result.add(feature)
            } else
            if (feature.hooks.afterAll.isStepPatternUsed(searchedStepPattern)) {
                result.add(feature)
            }
        }

        return result
    }

    private fun updateStepPatternForFeatureBeforeAllHooks(featureToUpdate: Feature, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): Feature {
        val updatedCalls = featureToUpdate.hooks.beforeAll.toMutableList()
        updatedCalls[stepCallIndexToUpdate] = updatedCalls[stepCallIndexToUpdate].copy(stepDef = newStep)

        return featureToUpdate.copy(hooks = featureToUpdate.hooks.copy(beforeAll = updatedCalls))
    }

    private fun updateStepPatternForFeatureBeforeEachHooks(featureToUpdate: Feature, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): Feature {
        val updatedCalls = featureToUpdate.hooks.beforeEach.toMutableList()
        updatedCalls[stepCallIndexToUpdate] = updatedCalls[stepCallIndexToUpdate].copy(stepDef = newStep)

        return featureToUpdate.copy(hooks = featureToUpdate.hooks.copy(beforeEach = updatedCalls))
    }

    private fun updateStepPatternForFeatureAfterEachHooks(featureToUpdate: Feature, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): Feature {
        val updatedCalls = featureToUpdate.hooks.afterEach.toMutableList()
        updatedCalls[stepCallIndexToUpdate] = updatedCalls[stepCallIndexToUpdate].copy(stepDef = newStep)

        return featureToUpdate.copy(hooks = featureToUpdate.hooks.copy(afterEach = updatedCalls))
    }

    private fun updateStepPatternForFeatureAfterAllHooks(featureToUpdate: Feature, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): Feature {
        val updatedCalls = featureToUpdate.hooks.afterAll.toMutableList()
        updatedCalls[stepCallIndexToUpdate] = updatedCalls[stepCallIndexToUpdate].copy(stepDef = newStep)

        return featureToUpdate.copy(hooks = featureToUpdate.hooks.copy(afterAll = updatedCalls))
    }

    private fun updateTestsThatUseOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef) {
        val testsThatUsesOldStep = findTestsThatCall(oldStep.stepPattern)

        for (testToUpdate in testsThatUsesOldStep) {
            var updatedTest = testToUpdate

            for ((index, stepCall) in testToUpdate.stepCalls.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedTest = updateStepPatternAtStepCallForTest(updatedTest, index, newStep)
                }
            }

            for ((index, stepCall) in testToUpdate.afterHooks.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedTest = updateStepPatternAtAfterHookForTest(updatedTest, index, newStep)
                }
            }

            saveTestWithoutReinitializingCaches(updatedTest, recursiveSave = false)
        }
    }

    private fun findTestsThatCall(searchedStepPattern: StepPattern): List<TestModel> {
        val result: MutableList<TestModel> = mutableListOf()

        val allTests = webProjectManager.getProjectServices().getTestsCache().getAllTests()

        for (test in allTests) {
            if (test.stepCalls.isStepPatternUsed(searchedStepPattern)) {
                result.add(test)
                continue
            }
            if (test.afterHooks.isStepPatternUsed(searchedStepPattern)) {
                result.add(test)
            }
        }

        return result
    }

    private fun updateStepPatternAtStepCallForTest(testToUpdate: TestModel, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): TestModel {
        val updatedCalls = testToUpdate.stepCalls.toMutableList()
        updatedCalls[stepCallIndexToUpdate] = updatedCalls[stepCallIndexToUpdate].copy(stepDef = newStep)

        return testToUpdate.copy(stepCalls = updatedCalls)
    }

    private fun updateStepPatternAtAfterHookForTest(testToUpdate: TestModel, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): TestModel {
        val updatedAfterHook = testToUpdate.afterHooks.toMutableList()
        updatedAfterHook[stepCallIndexToUpdate] = updatedAfterHook[stepCallIndexToUpdate].copy(stepDef = newStep)

        return testToUpdate.copy(afterHooks = updatedAfterHook)
    }

    private fun updateStepsThatUsesOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef) {
        val stepsThatUseOldStep = findStepsThatCall(oldStep)
        for (stepDefToUpdate in stepsThatUseOldStep) {
            var updatedStepDef = stepDefToUpdate
            for ((index, stepCall) in stepDefToUpdate.stepCalls.withIndex()) {
                if (stepCall.isCallOf(oldStep)) {
                    updatedStepDef = updateStepDefOfStepCall(updatedStepDef, index, newStep)
                }
            }

            saveComposedStepWithoutReinitializingCaches(updatedStepDef, recursiveSave = false)
        }
    }

    private fun findStepsThatCall(step: ComposedStepDef): List<ComposedStepDef> {
        val result: MutableSet<ComposedStepDef> = mutableSetOf()

        val composedSteps = stepsCache().getComposedSteps()
        for (composedStep in composedSteps) {
            for (stepCall in composedStep.stepCalls) {
                if (stepCall.isCallOf(step)) {
                    result.add(composedStep)
                }
            }
        }

        return result.toList()
    }

    private fun updateStepDefOfStepCall(stepDefToUpdate: ComposedStepDef, stepCallIndexToUpdate: Int, newStep: ComposedStepDef): ComposedStepDef {
        val updateStepCalls: MutableList<StepCall> = stepDefToUpdate.stepCalls.toMutableList()

        updateStepCalls[stepCallIndexToUpdate] = updateStepCalls[stepCallIndexToUpdate].copy(stepDef = newStep)

        return stepDefToUpdate.copy(stepCalls = updateStepCalls)
    }

    private fun validateComposedStepSave(composedStep: ComposedStepDef) {
        validateParameters(composedStep)
        validateComposedStepSaveUniquePattern(composedStep)
    }

    private fun validateParameters(composedStep: ComposedStepDef) {
        val parameters = composedStep.stepPattern.getParamStepPattern()

        for ((i, part) in parameters.withIndex()) {
            validateComposedStepParamName(part.name, i, composedStep)
        }
    }

    private fun validateComposedStepParamName(paramName: String, paramIndex: Int, composedStep: ComposedStepDef) {
        if (!FileStepDefSignatureParserFactory.isValidParameterName(paramName)) {
            throw ValidationException(
                    "invalid step parameter name [$paramName]" +
                    ": error found at parameter number ${paramIndex + 1} of composed step $composedStep"
            )
        }
    }

    private fun validateComposedStepSaveUniquePattern(composedStep: ComposedStepDef) {
        val oldPath = composedStep.oldPath
                ?: return // create

        val oldStep = stepsCache().getStepAtPath(oldPath) as? ComposedStepDef
                ?: return // update, but the old step was deleted in the mean time; handle it as create

        val oldStepPattern = oldStep.stepPattern
        val newStepPattern = composedStep.stepPattern

        if (oldStepPattern == newStepPattern) {
            return
        }

        val allSteps = stepsCache().getAllSteps()
        if (isOtherStepWithTheSameStepPatternAsTheNew(oldStepPattern, newStepPattern, allSteps)) {
            throw ValidationException(
                    "another step with the same pattern exists at path [${oldStep.path}]"
            )
        }
    }

    private fun saveStepCallsRecursively(stepCalls: List<StepCall>) {
        for (stepCall in stepCalls) {
            val composedStepDef = stepCall.stepDef as? ComposedStepDef
                    ?: continue

            saveComposedStep(composedStepDef)
        }
    }

    private fun saveExternalResources(composedStep: ComposedStepDef): ComposedStepDef {
        val stepCalls: List<StepCall> = composedStep.stepCalls.map { stepCall ->
            saveExternalResources(stepCall)
        }

        return composedStep.copy(stepCalls = stepCalls)
    }

    private fun saveTestExternalResources(testModel: TestModel): TestModel {
        val stepCalls: List<StepCall> = testModel.stepCalls.map { stepCall ->
            saveExternalResources(stepCall)
        }

        val afterHooks: List<StepCall> = testModel.afterHooks.map { stepCall ->
            saveExternalResources(stepCall)
        }

        return testModel.copy(stepCalls = stepCalls, afterHooks = afterHooks)
    }

    private fun saveFeatureExternalResources(feature: Feature): Feature {
        val beforeAll: List<StepCall> = feature.hooks.beforeAll.map { stepCall -> saveExternalResources(stepCall)}
        val beforeEach: List<StepCall> = feature.hooks.beforeEach.map { stepCall -> saveExternalResources(stepCall)}
        val afterEach: List<StepCall> = feature.hooks.afterEach.map { stepCall -> saveExternalResources(stepCall)}
        val afterAll: List<StepCall> = feature.hooks.afterAll.map { stepCall -> saveExternalResources(stepCall)}

        return feature.copy(hooks = feature.hooks.copy(
            beforeAll = beforeAll,
            beforeEach = beforeEach,
            afterEach = afterEach,
            afterAll = afterAll
        ))
    }

    private fun saveExternalResources(stepCall: StepCall): StepCall {
        val argsWithNewNames: List<Arg> = stepCall.args.map { arg ->
            saveExternalResource(arg)
        }

        return stepCall.copy(args = argsWithNewNames)
    }

    /**
     * if Arg does not represent an external resource, do nothing;
     * otherwise save it and return the new name
     */
    private fun saveExternalResource(arg: Arg): Arg {
        // todo: don't save if the content didn't change?
        val path: com.testerum.model.infrastructure.path.Path = arg.path
                ?: return arg // if we don't have a path, then this is an internal resource

        val resourceContext: ResourceContext = resourceFileService.save(
                resourceContext = ResourceContext(
                        oldPath = arg.oldPath,
                        path = path,
                        body = arg.content.orEmpty()
                ),
                resourcesDir = getResourcesDir()
        )

        val actualPath: com.testerum.model.infrastructure.path.Path = resourceContext.path

        return arg.copy(path = actualPath)
    }

    private fun reinitializeCaches() {
        // re-loading steps & tests to make sure tests are resolved properly
        // to optimize, we could re-load only the affected tests and/or steps
        webProjectManager.getProjectServices().reinitializeStepsCache()
        webProjectManager.getProjectServices().reinitializeTestsCache()
        webProjectManager.getProjectServices().reinitializeFeatureCache()
    }

}
