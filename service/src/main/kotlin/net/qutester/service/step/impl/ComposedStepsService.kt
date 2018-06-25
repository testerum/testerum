package net.qutester.service.step.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.stepdef.FileStepDefParserFactory
import com.testerum.test_file_format.stepdef.FileStepDefSerializer
import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.repository.enums.FileType
import net.qutester.model.step.ComposedStepDef
import net.qutester.service.mapper.FileToUiStepMapper
import net.qutester.service.mapper.UiToFileStepDefMapper
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange
import java.io.StringWriter

open class ComposedStepsService(val jsonObjectMapper: ObjectMapper,
                                val uiToFileStepDefMapper: UiToFileStepDefMapper,
                                val fileToUiStepMapper: FileToUiStepMapper,
                                val fileRepositoryService: FileRepositoryService) {

    companion object {
        private val COMPOSED_STEP_PARSER = ParserExecuter(FileStepDefParserFactory.stepDef())
    }

    fun getComposedSteps(): List<ComposedStepDef> {

        val steps: MutableList<ComposedStepDef> = mutableListOf();

        val allStepFiles = fileRepositoryService.getAllResourcesByType(FileType.STEP)
        for (stepFile in allStepFiles) {
            val stepDef = COMPOSED_STEP_PARSER.parse(stepFile.body)
            val composedStepDef: ComposedStepDef = fileToUiStepMapper.mapToUiModel(stepDef, stepFile)
            steps.add(composedStepDef)
        }

        return steps
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(
                knownPath = KnownPath(path, FileType.STEP)
        )
    }

    fun create(composedStepDef: ComposedStepDef): ComposedStepDef {
        val stepPath = Path(composedStepDef.path.directories, composedStepDef.getText(), FileType.STEP.fileExtension)

        val stepAsString = serializeComposedStepDefToFileFormat(composedStepDef)

        val createdRepositoryFile= fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(stepPath, FileType.STEP),
                                stepAsString
                        )
                )
        )

        val resolvedComposedStepDef = composedStepDef.copy(
                path = createdRepositoryFile.knownPath.asPath()
        )

        return resolvedComposedStepDef
    }

    fun update(composedStepDef: ComposedStepDef): ComposedStepDef {
        val oldStepPath = composedStepDef.path
        val newStepPath = Path(oldStepPath.directories, composedStepDef.getText(), FileType.STEP.fileExtension)

        val stepAsString = serializeComposedStepDefToFileFormat(composedStepDef)

        fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(oldStepPath, FileType.STEP),
                        RepositoryFile(
                                KnownPath(newStepPath, FileType.STEP),
                                stepAsString
                        )
                )
        )

        val resolvedComposedStepDef = composedStepDef.copy(
                path = newStepPath
        )

        return resolvedComposedStepDef
    }

    private fun serializeComposedStepDefToFileFormat(composedStepDef: ComposedStepDef): String {
        val fileStepDef = uiToFileStepDefMapper.mapToFileModel(composedStepDef)
        val destination = StringWriter()
        FileStepDefSerializer.serialize(fileStepDef, destination, 0)
        val stepAsString = destination.toString()
        return stepAsString
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        return fileRepositoryService.renameDirectory(
                KnownPath(renamePath.path, FileType.STEP),
                renamePath.newName
        )
    }

    fun deleteDirectory(pathToDelete: Path) {
        fileRepositoryService.delete(
                KnownPath(pathToDelete, FileType.STEP)
        )
    }

    fun moveDirectoryOrFile(copyPath: CopyPath) {
        fileRepositoryService.moveDirectoryOrFile(
                copyPath = KnownPath(copyPath.copyPath, FileType.STEP),
                destinationKnownPath = KnownPath(copyPath.destinationPath, FileType.STEP)
        )
    }
}