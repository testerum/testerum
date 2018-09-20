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
import com.testerum.model.step.StepDef
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.util.isOtherStepWithTheSameStepPatternAsTheNew
import com.testerum.web_backend.util.isTestUsingStepPattern
import java.nio.file.Path

class SaveFrontendService(private val frontendDirs: FrontendDirs,
                          private val testsCache: TestsCache,
                          private val stepsCache: StepsCache,
                          private val resourceFileService: ResourceFileService) {

    fun saveTest(test: TestModel): TestModel {
        val repositoryDir = frontendDirs.getRequiredRepositoryDir()

        return saveTest(test, repositoryDir)
    }

    private fun saveTest(test: TestModel, repositoryDir: Path): TestModel {
        saveStepCallsRecursively(test.stepCalls, repositoryDir)

        val testWithSavedExternalResources = saveExternalResources(test, repositoryDir)

        return testsCache.save(testWithSavedExternalResources)
    }

    fun saveComposedStep(composedStep: ComposedStepDef): ComposedStepDef {
        val repositoryDir = frontendDirs.getRepositoryDir()
                ?: throw java.lang.IllegalStateException("cannot save composed step, because the setting [${SystemSettingKeys.REPOSITORY_DIR}] is not set")

        return saveComposedStep(composedStep, repositoryDir)
    }

    private fun saveComposedStep(composedStep: ComposedStepDef, repositoryDir: Path): ComposedStepDef {
        validateComposedStepSave(composedStep)

        saveStepCallsRecursively(composedStep.stepCalls, repositoryDir)

        val stepWithSavedExternalResources: ComposedStepDef = saveExternalResources(composedStep, repositoryDir)

        val existingStep = stepWithSavedExternalResources.oldPath?.let { stepsCache.getComposedStepAtPath(it) }

        val savedComposedStep = stepsCache.saveComposedStep(stepWithSavedExternalResources)

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
                    updatedTest = updateStepPatternAtStepCallForTest(updatedTest, index, newStep.stepPattern)
                }
            }

            saveTest(updatedTest, repositoryDir)
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

    private fun updateStepPatternAtStepCallForTest(testToUpdate: TestModel, stepCallIndexToUpdate: Int, stepPattern: StepPattern): TestModel {
        val updatedCalls = testToUpdate.stepCalls.toMutableList()
        val updatedDef = (updatedCalls[stepCallIndexToUpdate].stepDef as ComposedStepDef).copy(stepPattern = stepPattern)
        updatedCalls[stepCallIndexToUpdate] = updatedCalls[stepCallIndexToUpdate].copy(stepDef = updatedDef)

        return testToUpdate.copy(stepCalls = updatedCalls)
    }

    private fun updateStepsThatUsesOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef, repositoryDir: Path) {
        val stepsThatUseOldStep = findStepsThatCall(oldStep.stepPattern)
        for (stepDefToUpdate in stepsThatUseOldStep) {
            var updatedStepDef = stepDefToUpdate
            for ((index, stepCall) in stepDefToUpdate.stepCalls.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedStepDef = updateStepPatternAtStepCall(updatedStepDef, index, newStep.stepPattern)
                }
            }

            saveComposedStep(updatedStepDef, repositoryDir)
        }
    }

    private fun findStepsThatCall(searchedStepPattern: StepPattern): List<ComposedStepDef> {
        val result: MutableSet<ComposedStepDef> = mutableSetOf()

        val composedSteps = stepsCache.getComposedSteps()
        for (composedStep in composedSteps) {
            for (stepCall in composedStep.stepCalls) {
                if (stepCall.stepDef.stepPattern == searchedStepPattern) {
                    result.add(composedStep)
                }
            }
        }

        return result.toList()
    }

    private fun updateStepPatternAtStepCall(stepDefToUpdate: ComposedStepDef, stepCallIndexToUpdate: Int, stepPattern: StepPattern): ComposedStepDef {
        val updateStepCalls: MutableList<StepCall> = stepDefToUpdate.stepCalls.toMutableList()
        val updateStepCallStepDef: StepDef = (updateStepCalls[stepCallIndexToUpdate].stepDef as ComposedStepDef).copy(stepPattern = stepPattern)
        updateStepCalls[stepCallIndexToUpdate] = updateStepCalls[stepCallIndexToUpdate].copy(stepDef = updateStepCallStepDef)

        return stepDefToUpdate.copy(stepCalls = updateStepCalls)
    }

    private fun validateComposedStepSave(composedStep: ComposedStepDef) {
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
}
