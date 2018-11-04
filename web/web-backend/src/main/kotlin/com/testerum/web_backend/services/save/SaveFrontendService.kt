package com.testerum.web_backend.services.save

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
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
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureParserFactory
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.initializers.caches.impl.StepsCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.TestsCacheInitializer
import com.testerum.web_backend.util.isOtherStepWithTheSameStepPatternAsTheNew
import com.testerum.web_backend.util.isTestUsingStepPattern
import java.nio.file.Path

class SaveFrontendService(private val frontendDirs: FrontendDirs,
                          private val stepsCache: StepsCache,
                          private val stepsCacheInitializer: StepsCacheInitializer,
                          private val testsCache: TestsCache,
                          private val testsCacheInitializer: TestsCacheInitializer,
                          private val resourceFileService: ResourceFileService) {

    fun saveTest(test: TestModel): TestModel {
        val repositoryDir = frontendDirs.getRequiredRepositoryDir()

        val savedTest = saveTest(test, repositoryDir)

        reinitializeCaches()

        return savedTest
    }

    private fun saveTest(test: TestModel, repositoryDir: Path, recursiveSave: Boolean = true): TestModel {
        if (recursiveSave) {
            saveStepCallsRecursively(test.stepCalls, repositoryDir)
        }

        val testToSave = if (recursiveSave) {
            saveExternalResources(test, repositoryDir)
        } else {
            test
        }

        return testsCache.save(testToSave)
    }

    fun saveComposedStep(composedStep: ComposedStepDef): ComposedStepDef {
        val repositoryDir = frontendDirs.getRepositoryDir()
                ?: throw java.lang.IllegalStateException("cannot save composed step, because the setting [${SystemSettingKeys.REPOSITORY_DIR}] is not set")

        val savedStep = saveComposedStep(composedStep, repositoryDir)

        reinitializeCaches()

        return savedStep
    }

    private fun saveComposedStep(composedStep: ComposedStepDef, repositoryDir: Path, recursiveSave: Boolean = true): ComposedStepDef {
        validateComposedStepSave(composedStep)

        if (recursiveSave) {
            saveStepCallsRecursively(composedStep.stepCalls, repositoryDir)
        }

        val stepToSave: ComposedStepDef = if (recursiveSave) {
            saveExternalResources(composedStep, repositoryDir)
        } else {
            composedStep
        }

        val existingStep = stepToSave.oldPath?.let { stepsCache.getComposedStepAtPath(it) }

        val savedComposedStep = stepsCache.saveComposedStep(stepToSave)

        // update affected tests & steps
        if (existingStep != null) {
            if (composedStep.isChangedRequiringSave(existingStep)) {
                val existingStepPattern = existingStep.stepPattern
                val newStepPattern = savedComposedStep.stepPattern

                if (existingStepPattern.isStepPatternChangeCompatible(newStepPattern)) {
                    updateTestsThatUseOldStep(existingStep, savedComposedStep, repositoryDir)
                    updateStepsThatUsesOldStep(existingStep, savedComposedStep, repositoryDir)
                }
            }
        }

        return savedComposedStep
    }

    private fun updateTestsThatUseOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef, repositoryDir: Path) {
        val testsThatUsesOldStep = findTestsThatCall(oldStep.stepPattern)

        for (testToUpdate in testsThatUsesOldStep) {
            var updatedTest = testToUpdate
            for ((index, stepCall) in testToUpdate.stepCalls.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedTest = updateStepPatternAtStepCallForTest(updatedTest, index, newStep)
                }
            }

            saveTest(updatedTest, repositoryDir, recursiveSave = false)
        }
    }

    private fun findTestsThatCall(searchedStepPattern: StepPattern): List<TestModel> {
        val result: MutableList<TestModel> = mutableListOf()

        val allTests = testsCache.getAllTests()

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

    private fun updateStepsThatUsesOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef, repositoryDir: Path) {
        val stepsThatUseOldStep = findStepsThatCall(oldStep)
        for (stepDefToUpdate in stepsThatUseOldStep) {
            var updatedStepDef = stepDefToUpdate
            for ((index, stepCall) in stepDefToUpdate.stepCalls.withIndex()) {
                if (stepCall.isCallOf(oldStep)) {
                    updatedStepDef = updateStepDefOfStepCall(updatedStepDef, index, newStep)
                }
            }

            saveComposedStep(updatedStepDef, repositoryDir, recursiveSave = false)
        }
    }

    private fun findStepsThatCall(step: ComposedStepDef): List<ComposedStepDef> {
        val result: MutableSet<ComposedStepDef> = mutableSetOf()

        val composedSteps = stepsCache.getComposedSteps()
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

        val oldStep = stepsCache.getStepAtPath(oldPath) as? ComposedStepDef
                ?: return // update, but the old step was deleted in the mean time; handle it as create

        val oldStepPattern = oldStep.stepPattern
        val newStepPattern = composedStep.stepPattern

        if (oldStepPattern == newStepPattern) {
            return
        }

        val allSteps = stepsCache.getAllSteps()
        if (isOtherStepWithTheSameStepPatternAsTheNew(oldStepPattern, newStepPattern, allSteps)) {
            throw ValidationException(
                    "another step with the same pattern exists at path [${oldStep.path}]"
            )
        }
    }

    private fun saveStepCallsRecursively(stepCalls: List<StepCall>, repositoryDir: Path) {
        for (stepCall in stepCalls) {
            val composedStepDef = stepCall.stepDef as? ComposedStepDef
                    ?: continue

            saveComposedStep(composedStepDef, repositoryDir)
        }
    }

    private fun saveExternalResources(composedStep: ComposedStepDef, repositoryDir: Path): ComposedStepDef {
        val stepCalls: List<StepCall> = composedStep.stepCalls.map { stepCall ->
            saveExternalResources(stepCall, repositoryDir)
        }

        return composedStep.copy(stepCalls = stepCalls)
    }

    private fun saveExternalResources(testModel: TestModel, repositoryDir: Path): TestModel {
        val stepCalls: List<StepCall> = testModel.stepCalls.map { stepCall ->
            saveExternalResources(stepCall, repositoryDir)
        }

        return testModel.copy(stepCalls = stepCalls)
    }

    private fun saveExternalResources(stepCall: StepCall, repositoryDir: Path): StepCall {
        val argsWithNewNames: List<Arg> = stepCall.args.map { arg ->
            saveExternalResource(arg, repositoryDir)
        }

        return stepCall.copy(args = argsWithNewNames)
    }

    /**
     * if Arg does not represent an external resource, do nothing;
     * otherwise save it and return the new name
     */
    private fun saveExternalResource(arg: Arg, repositoryDir: Path): Arg {
        // todo: don't save if the content didn't change?
        val path: com.testerum.model.infrastructure.path.Path = arg.path
                ?: return arg // if we don't have a path, then this is an internal resource

        val resourcesDir = frontendDirs.getResourcesDir(repositoryDir)

        val resourceContext: ResourceContext = resourceFileService.save(
                resourceContext = ResourceContext(
                        oldPath = arg.oldPath,
                        path = path,
                        body = arg.content.orEmpty()
                ),
                resourcesDir = resourcesDir
        )

        val actualPath: com.testerum.model.infrastructure.path.Path = resourceContext.path

        return arg.copy(path = actualPath)
    }

    private fun reinitializeCaches() {
        // re-loading steps & tests to make sure tests are resolved properly
        // to optimize, we could re-load only the affected tests and/or steps
        stepsCacheInitializer.reinitializeComposedSteps()
        testsCacheInitializer.initialize()
    }

}
