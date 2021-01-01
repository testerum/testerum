package com.testerum.web_backend.services.save

import com.testerum.file_service.file.ResourceFileService
import com.testerum.file_service.util.isChangedRequiringSave
import com.testerum.file_service.util.isStepPatternChangeCompatible
import com.testerum.model.arg.Arg
import com.testerum.model.exception.ValidationException
import com.testerum.model.resources.ResourceContext
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureParserFactory
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.util.isOtherStepWithTheSameStepPatternAsTheNew
import com.testerum.web_backend.util.isTestUsingStepPattern

class SaveFrontendService(private val webProjectManager: WebProjectManager,
                          private val resourceFileService: ResourceFileService) {

    private fun stepsCache() = webProjectManager.getProjectServices().getStepsCache()

    private fun getResourcesDir() = webProjectManager.getProjectServices().dirs().getResourcesDir()

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
            saveExternalResources(test)
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
                    updateTestsThatUseOldStep(existingStep, savedComposedStep)
                    updateStepsThatUsesOldStep(existingStep, savedComposedStep)
                }
            }
        }

        return savedComposedStep
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

            saveTestWithoutReinitializingCaches(updatedTest, recursiveSave = false)
        }
    }

    private fun findTestsThatCall(searchedStepPattern: StepPattern): List<TestModel> {
        val result: MutableList<TestModel> = mutableListOf()

        val allTests = webProjectManager.getProjectServices().getTestsCache().getAllTests()

        for (test in allTests) {
            if (test.isTestUsingStepPattern(searchedStepPattern)) {
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

    private fun saveExternalResources(testModel: TestModel): TestModel {
        val stepCalls: List<StepCall> = testModel.stepCalls.map { stepCall ->
            saveExternalResources(stepCall)
        }

        return testModel.copy(stepCalls = stepCalls)
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
    }

}
