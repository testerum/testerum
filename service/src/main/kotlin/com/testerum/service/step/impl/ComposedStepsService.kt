package com.testerum.service.step.impl

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.service.file_repository.FileRepositoryService
import com.testerum.service.file_repository.model.KnownPath
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.repository.enums.FileType
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.builder.ComposedStepDirectoryTreeBuilder
import com.testerum.service.mapper.FileToUiStepMapper
import com.testerum.service.warning.WarningService
import com.testerum.test_file_format.stepdef.FileStepDefParserFactory
import java.nio.file.Files

open class ComposedStepsService(private val fileToUiStepMapper: FileToUiStepMapper,
                                private val fileRepositoryService: FileRepositoryService,
                                private val warningService: WarningService) {

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
