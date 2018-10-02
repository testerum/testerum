package com.testerum.web_backend.services.steps

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.web_backend.services.save.SaveFrontendService
import com.testerum.web_backend.services.steps.filterer.StepsTreeFilterer

class ComposedStepsFrontendService(private val stepsCache: StepsCache,
                                   private val composedStepUpdateCompatibilityFrontendService: ComposedStepUpdateCompatibilityFrontendService,
                                   private val saveFrontendService: SaveFrontendService,
                                   private val warningService: WarningService) {

    fun getComposedSteps(filter: StepsTreeFilter): List<ComposedStepDef> {
        return stepsCache.getAllSteps()
                .filterIsInstance(ComposedStepDef::class.java)
                .filter { StepsTreeFilterer.matches(it, filter) }
    }

    fun getComposedStepAtPath(path: Path): ComposedStepDef? {
        return stepsCache.getComposedStepAtPath(path)
    }

    fun deleteComposedStep(path: Path) {
        stepsCache.deleteComposedStep(path)
    }

    fun checkUpdateCompatibility(composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return composedStepUpdateCompatibilityFrontendService.checkUpdateCompatibility(composedStepDef)
    }

    fun saveComposedStep(composedStepDef: ComposedStepDef): ComposedStepDef {
        return saveFrontendService.saveComposedStep(composedStepDef)
    }

    fun renameComposedStepDirectory(renamePath: RenamePath): Path {
        return stepsCache.renameComposedStepDirectory(renamePath)
    }

    fun deleteComposedStepDirectory(path: Path) {
        stepsCache.deleteComposedStepDirectory(path)
    }

    fun moveComposedStepDirectoryOrFile(copyPath: CopyPath) {
        stepsCache.moveComposedStepDirectoryOrFile(copyPath)
    }

    fun getDirectoriesTree(): ComposedContainerStepNode = stepsCache.getComposedStepsDirectoriesTree()

    fun getWarnings(composedStep: ComposedStepDef): ComposedStepDef {
        return warningService.composedStepWithWarnings(composedStep)
    }

}
