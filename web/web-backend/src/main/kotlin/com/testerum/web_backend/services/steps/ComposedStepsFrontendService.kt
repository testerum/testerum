package com.testerum.web_backend.services.steps

import com.testerum.common_kotlin.walk
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.step.operation.response.CheckComposedStepDefUsageResponse
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.services.save.SaveFrontendService
import com.testerum.web_backend.services.steps.builder.ComposedStepDirectoryTreeBuilder
import com.testerum.web_backend.services.steps.filterer.StepsTreeFilterer
import java.nio.file.Files

class ComposedStepsFrontendService(
    private val webProjectManager: WebProjectManager,
    private val composedStepUpdateCompatibilityFrontendService: ComposedStepUpdateCompatibilityFrontendService,
    private val saveFrontendService: SaveFrontendService,
    private val warningService: WarningService
) {

    private fun stepsCache() = webProjectManager.getProjectServices().getStepsCache()

    fun getComposedSteps(filter: StepsTreeFilter): List<ComposedStepDef> {
        return stepsCache().getAllSteps()
            .filterIsInstance(ComposedStepDef::class.java)
            .filter { StepsTreeFilterer.matches(it, filter) }
    }

    fun getComposedStepAtPath(path: Path): ComposedStepDef? {
        val composedStep = stepsCache().getComposedStepAtPath(path)

        if (composedStep != null) {
            val stepUsed = composedStepUpdateCompatibilityFrontendService.isStepUsed(composedStep)
            if (!stepUsed) {
                return composedStep.copy(isUsed = stepUsed)
            }
        }
        return composedStep
    }

    fun deleteComposedStep(path: Path) {
        stepsCache().deleteComposedStep(path)

        reinitializeCaches()
    }

    fun checkUpdateCompatibility(composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return composedStepUpdateCompatibilityFrontendService.checkUpdateCompatibility(composedStepDef)
    }

    fun checkUsage(composedStepDef: ComposedStepDef): CheckComposedStepDefUsageResponse {
        return composedStepUpdateCompatibilityFrontendService.checkUsage(composedStepDef)
    }

    fun saveComposedStep(composedStepDef: ComposedStepDef): ComposedStepDef {
        return saveFrontendService.saveComposedStep(composedStepDef)
    }

    fun renameComposedStepDirectory(renamePath: RenamePath): Path {
        val result = stepsCache().renameComposedStepDirectory(renamePath)

        reinitializeCaches()

        return result
    }

    fun deleteComposedStepDirectory(path: Path) {
        stepsCache().deleteComposedStepDirectory(path)

        reinitializeCaches()
    }

    fun moveComposedStepDirectoryOrFile(copyPath: CopyPath) {
        stepsCache().moveComposedStepDirectoryOrFile(copyPath)

        reinitializeCaches()
    }

    fun getDirectoriesTree(): ComposedContainerStepNode {
        val composedStepsDir = webProjectManager.getProjectServices().dirs().getComposedStepsDir()

        val treeBuilder = ComposedStepDirectoryTreeBuilder()

        composedStepsDir.walk { path ->
            if (Files.isDirectory(path) && (path != composedStepsDir)) {
                val relativeDirectory = composedStepsDir.relativize(path)
                val relativeDirectoryPathParts: List<String> = relativeDirectory.map { it.fileName.toString() }

                treeBuilder.addComposedStepDirectory(relativeDirectoryPathParts)
            }
        }

        return treeBuilder.createTree()
    }

    fun getWarnings(composedStep: ComposedStepDef): ComposedStepDef {
        return warningService.composedStepWithWarnings(composedStep)
    }

    private fun reinitializeCaches() {
        // re-loading steps & tests to make sure tests are resolved properly
        // to optimize, we could re-load only the affected tests and/or steps
        webProjectManager.getProjectServices().reinitializeStepsCache()
        webProjectManager.getProjectServices().reinitializeTestsCache()
    }

    fun copyComposedStep(sourcePath: Path, destinationDirPath: Path): Path {
        return webProjectManager.getProjectServices().getStepsCache()
            .copyComposedStep(sourcePath, destinationDirPath)
    }

    fun moveComposedStep(sourcePath: Path, destinationDirPath: Path): Path {
        return webProjectManager.getProjectServices().getStepsCache()
            .moveComposedStep(sourcePath, destinationDirPath)
    }
}
