package com.testerum.service.step.impl

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.file_repository.model.RepositoryFile
import com.testerum.file_repository.model.RepositoryFileChange
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.repository.enums.FileType
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.builder.ComposedStepDirectoryTreeBuilder
import com.testerum.service.mapper.FileToUiStepMapper
import com.testerum.service.mapper.UiToFileStepDefMapper
import com.testerum.service.warning.WarningService
import com.testerum.test_file_format.stepdef.FileStepDefParserFactory
import com.testerum.test_file_format.stepdef.FileStepDefSerializer
import java.io.StringWriter
import java.nio.file.Files

open class ComposedStepsService(val uiToFileStepDefMapper: UiToFileStepDefMapper,
                                val fileToUiStepMapper: FileToUiStepMapper,
                                val fileRepositoryService: FileRepositoryService,
                                val warningService: WarningService) {

    companion object {
        private val COMPOSED_STEP_PARSER = ParserExecuter(FileStepDefParserFactory.stepDef())
    }

    fun getComposedSteps(): List<ComposedStepDef> {
        val steps: MutableList<ComposedStepDef> = mutableListOf()

        val allStepFiles = fileRepositoryService.getAllResourcesByType(FileType.COMPOSED_STEP)
        for (stepFile in allStepFiles) {
            val stepDef = COMPOSED_STEP_PARSER.parse(stepFile.body)
            val composedStepDef: ComposedStepDef = fileToUiStepMapper.mapToUiModel(stepDef, stepFile)
            steps.add(composedStepDef)
        }

        return steps
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(
                knownPath = KnownPath(path, FileType.COMPOSED_STEP)
        )
    }

    fun create(composedStepDef: ComposedStepDef): ComposedStepDef {
        val stepPath = Path(composedStepDef.path.directories, composedStepDef.getText(), FileType.COMPOSED_STEP.fileExtension)

        val stepAsString = serializeComposedStepDefToFileFormat(composedStepDef)

        val createdRepositoryFile= fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(stepPath, FileType.COMPOSED_STEP),
                                stepAsString
                        )
                )
        )

        return composedStepDef.copy(
                path = createdRepositoryFile.knownPath.asPath()
        )
    }

    fun update(composedStepDef: ComposedStepDef): ComposedStepDef {
        val oldStepPath = composedStepDef.path
        val newStepPath = Path(oldStepPath.directories, composedStepDef.getText(), FileType.COMPOSED_STEP.fileExtension)

        val stepAsString = serializeComposedStepDefToFileFormat(composedStepDef)

        fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(oldStepPath, FileType.COMPOSED_STEP),
                        RepositoryFile(
                                KnownPath(newStepPath, FileType.COMPOSED_STEP),
                                stepAsString
                        )
                )
        )

        return composedStepDef.copy(path = newStepPath)
    }

    private fun serializeComposedStepDefToFileFormat(composedStepDef: ComposedStepDef): String {
        val fileStepDef = uiToFileStepDefMapper.mapToFileModel(composedStepDef)
        val destination = StringWriter()
        FileStepDefSerializer.serialize(fileStepDef, destination, 0)

        return destination.toString()
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        return fileRepositoryService.renameDirectory(
                KnownPath(renamePath.path, FileType.COMPOSED_STEP),
                renamePath.newName
        )
    }

    fun deleteDirectory(pathToDelete: Path) {
        fileRepositoryService.delete(
                KnownPath(pathToDelete, FileType.COMPOSED_STEP)
        )
    }

    fun moveDirectoryOrFile(copyPath: CopyPath) {
        fileRepositoryService.moveDirectoryOrFile(
                copyPath = KnownPath(copyPath.copyPath, FileType.COMPOSED_STEP),
                destinationKnownPath = KnownPath(copyPath.destinationPath, FileType.COMPOSED_STEP)
        )
    }

    fun getDirectoriesTree(): ComposedContainerStepNode {
        val treeBuilder = ComposedStepDirectoryTreeBuilder()

        fileRepositoryService.walkDirectoryTree(FileType.COMPOSED_STEP) { rootDir, path ->
            if (Files.isDirectory(path) && (path != rootDir)) {
                val relativeDirectory = rootDir.relativize(path)
                val relativeDirectoryPathParts: List<String> = relativeDirectory.map { it.fileName.toString() }

                treeBuilder.addComposedStepDirectory(relativeDirectoryPathParts)
            }
        }

        return treeBuilder.build()
    }

    fun getWarnings(composedStepDef: ComposedStepDef, keepExistingWarnings: Boolean): ComposedStepDef
            = warningService.composedStepWithWarnings(composedStepDef, keepExistingWarnings)

}