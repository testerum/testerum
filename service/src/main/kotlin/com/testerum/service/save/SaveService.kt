package com.testerum.service.save

import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.file_repository.model.RepositoryFile
import com.testerum.file_repository.model.RepositoryFileChange
import com.testerum.model.arg.Arg
import com.testerum.model.exception.ServerStateChangedException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.service.mapper.UiToFileStepDefMapper
import com.testerum.service.mapper.UiToFileTestMapper
import com.testerum.service.resources.ResourcesService
import com.testerum.service.save.changed_checks.isChangedRequiringSave
import com.testerum.service.step.StepCache
import com.testerum.service.step.StepUpdateCompatibilityService
import com.testerum.service.step.util.isStepPatternChangeCompatible
import com.testerum.service.tests.TestsService
import com.testerum.service.warning.WarningService
import com.testerum.test_file_format.stepdef.FileStepDefSerializer
import com.testerum.test_file_format.testdef.FileTestDefSerializer
import java.io.StringWriter

class SaveService(private val fileRepositoryService: FileRepositoryService,
                  private val testsService: TestsService,
                  private val uiToFileTestMapper: UiToFileTestMapper,
                  private val stepUpdateCompatibilityService: StepUpdateCompatibilityService,
                  private val stepCache: StepCache,
                  private val uiToFileStepDefMapper: UiToFileStepDefMapper,
                  private val resourcesService: ResourcesService,
                  private val warningService: WarningService) {

    fun saveTest(testModel: TestModel): TestModel {
        println("SaveService.saveTest($testModel)")
        val pathOfTestToReturn: Path

        saveChildren(testModel.stepCalls)

        val testWithSavedExternalResources: TestModel = saveExternalResources(testModel)

        val existingTest = testWithSavedExternalResources.oldPath?.let { testsService.getTestAtPath(it) }
        if (testWithSavedExternalResources.isChangedRequiringSave(existingTest)) {
            val oldKnownPath = testWithSavedExternalResources.oldPath?.let {
                KnownPath(it, FileType.TEST)
            }

            val newPath = Path(
                    directories = testWithSavedExternalResources.path.directories,
                    fileName = testWithSavedExternalResources.text,
                    fileExtension = FileType.TEST.fileExtension
            )

            val serializedTest = serializeTest(testWithSavedExternalResources)

            println("SaveService.saveTest-save file($testModel)")
            val repositoryFile = fileRepositoryService.save(
                    RepositoryFileChange(
                            oldKnownPath,
                            RepositoryFile(
                                    KnownPath(newPath, FileType.TEST),
                                    serializedTest
                            )
                    )
            )

            pathOfTestToReturn = repositoryFile.knownPath.asPath()
        } else {
            pathOfTestToReturn = testWithSavedExternalResources.path
        }

        return testsService.getTestAtPath(pathOfTestToReturn)!!
    }

    /**
     * @return a test with external resources saved;
     *         the file names of the external resource might have been changed, to make them filesystem-friendly
     */
    private fun saveExternalResources(testModel: TestModel): TestModel {
        val stepCalls: List<StepCall> = testModel.stepCalls.map { stepCall ->
            saveExternalResources(stepCall)
        }

        return testModel.copy(stepCalls = stepCalls)
    }

    private fun saveExternalResources(composedStepDef: ComposedStepDef): ComposedStepDef {
        val stepCalls: List<StepCall> = composedStepDef.stepCalls.map { stepCall ->
            saveExternalResources(stepCall)
        }

        return composedStepDef.copy(stepCalls = stepCalls)
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
        val path: Path = arg.path
                ?: return arg // if we don't have a path, then this is an internal resource

        println("====================+> saving external resource [$arg]")

        val resourceContext: ResourceContext = resourcesService.save(
                ResourceContext(
                        oldPath = arg.oldPath,
                        path = path,
                        body = arg.content.orEmpty()
                )
        )

        val actualPath: Path = resourceContext.path

        return arg.copy(path = actualPath)
    }

    private fun saveChildren(stepCalls: List<StepCall>) {
        println("SaveService.saveChildren($stepCalls)")
        for (stepCall in stepCalls) {
            val stepDef = stepCall.stepDef as? ComposedStepDef
                    ?: continue

            saveComposedStep(stepDef)
        }
    }

    fun saveComposedStep(stepDef: ComposedStepDef): ComposedStepDef {
        println("SaveService.saveComposedStep($stepDef)")
        val stepToReturn: ComposedStepDef

        saveChildren(stepDef.stepCalls)

        val stepWithSavedExternalResources: ComposedStepDef = saveExternalResources(stepDef)

        val existingStepDef = stepWithSavedExternalResources.oldPath?.let { stepCache.getComposedStepAtPath(it) }
        if (stepWithSavedExternalResources.isChangedRequiringSave(existingStepDef)) {
            if (existingStepDef != null) {
                val existingStepPattern = existingStepDef.stepPattern
                val newStepPattern = stepWithSavedExternalResources.stepPattern

                if (existingStepPattern != newStepPattern) {
                    if (stepUpdateCompatibilityService.isOtherStepWithTheSameStepPattern(existingStepPattern, newStepPattern)) {
                        throw ServerStateChangedException("another step with the same pattern exists") // todo: specify the path to the other pattern
                    }

                    if (existingStepPattern.isStepPatternChangeCompatible(newStepPattern)) {
                        updateTestsThatUseOldStep(existingStepDef, stepWithSavedExternalResources)
                        updateStepsThatUsesOldStep(existingStepDef, stepWithSavedExternalResources)
                    }
                }
            }

            val savedStep = saveComposedStepFile(stepWithSavedExternalResources)
            val cachedStep = stepCache.save(savedStep)

            stepToReturn = cachedStep
        } else {
            stepToReturn = stepWithSavedExternalResources
        }

        return warningService.composedStepWithWarnings(stepToReturn, keepExistingWarnings = false)
    }

    private fun updateStepsThatUsesOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef) {
        val stepsThatUsesOldStep = stepUpdateCompatibilityService.findStepsThatUsesStepPatternAsDirectChild(oldStep.stepPattern)
        for (stepDefToUpdate in stepsThatUsesOldStep) {
            var updatedStepDef = stepDefToUpdate
            for ((index, stepCall) in stepDefToUpdate.stepCalls.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedStepDef = updateStepPatternAtStepCall(updatedStepDef, index, newStep.stepPattern)
                }
            }

            saveComposedStepFile(updatedStepDef)
        }
    }

    private fun saveComposedStepFile(composedStepDef: ComposedStepDef): ComposedStepDef {
        println("SaveService.saveComposedStepFile($composedStepDef)")
        val path = composedStepDef.path

        val newStepPath = Path(path.directories, composedStepDef.getText(), FileType.COMPOSED_STEP.fileExtension)

        val oldStepPath = if (composedStepDef.oldPath == null) {
            newStepPath // create
        } else {
            path // update
        }

        val stepAsString = serializeComposedStepDefToFileFormat(composedStepDef)

        val repositoryFile = fileRepositoryService.save(
                RepositoryFileChange(
                        KnownPath(oldStepPath, FileType.COMPOSED_STEP),
                        RepositoryFile(
                                KnownPath(newStepPath, FileType.COMPOSED_STEP),
                                stepAsString
                        )
                )
        )

        return composedStepDef.copy(
                path = repositoryFile.knownPath.asPath()
        )
    }

    private fun serializeComposedStepDefToFileFormat(composedStepDef: ComposedStepDef): String {
        val fileStepDef = uiToFileStepDefMapper.mapToFileModel(composedStepDef)
        val destination = StringWriter()
        FileStepDefSerializer.serialize(fileStepDef, destination, 0)

        return destination.toString()
    }

    private fun updateStepPatternAtStepCall(stepDefToUpdate: ComposedStepDef, stepCallIndexToUpdate: Int, stepPattern: StepPattern): ComposedStepDef {
        val updateStepCalls: MutableList<StepCall> = stepDefToUpdate.stepCalls.toMutableList()
        val updateStepCallStepDef: StepDef = (updateStepCalls[stepCallIndexToUpdate].stepDef as ComposedStepDef).copy(stepPattern = stepPattern)
        updateStepCalls[stepCallIndexToUpdate] = updateStepCalls[stepCallIndexToUpdate].copy(stepDef = updateStepCallStepDef)
        return stepDefToUpdate.copy(stepCalls = updateStepCalls)
    }

    private fun updateTestsThatUseOldStep(oldStep: ComposedStepDef, newStep: ComposedStepDef) {
        val testsThatUsesOldStep = stepUpdateCompatibilityService.findTestsThatUsesStepPatternAsChild(oldStep.stepPattern)

        for (testToUpdate in testsThatUsesOldStep) {
            var updatedTest = testToUpdate
            for ((index, stepCall) in testToUpdate.stepCalls.withIndex()) {
                if (stepCall.stepDef.stepPattern == oldStep.stepPattern) {
                    updatedTest = updateStepPatternAtStepCallForTest(updatedTest, index, newStep.stepPattern)
                }
            }

            saveTest(updatedTest)
        }
    }

    private fun updateStepPatternAtStepCallForTest(testToUpdate: TestModel, stepCallIndexToUpdate: Int, stepPattern: StepPattern): TestModel {
        val updateStepCalls: MutableList<StepCall> = testToUpdate.stepCalls.toMutableList()
        val updateStepCallStepDef: StepDef = (updateStepCalls[stepCallIndexToUpdate].stepDef as ComposedStepDef).copy(stepPattern = stepPattern)
        updateStepCalls[stepCallIndexToUpdate] = updateStepCalls[stepCallIndexToUpdate].copy(stepDef = updateStepCallStepDef)

        return testToUpdate.copy(stepCalls = updateStepCalls)
    }

    private fun serializeTest(test: TestModel): String {
        val fileTest = uiToFileTestMapper.mapToFileModel(test)

        return FileTestDefSerializer.serializeToString(fileTest)
    }

}
