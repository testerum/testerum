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
import com.testerum.web_backend.services.initializers.caches.impl.StepsCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.TestsCacheInitializer
import com.testerum.web_backend.services.save.SaveFrontendService
import com.testerum.web_backend.services.steps.filterer.StepsTreeFilterer

class ComposedStepsFrontendService(private val stepsCache: StepsCache,
                                   private val composedStepUpdateCompatibilityFrontendService: ComposedStepUpdateCompatibilityFrontendService,
                                   private val saveFrontendService: SaveFrontendService,
                                   private val warningService: WarningService,
                                   private val stepsCacheInitializer: StepsCacheInitializer,
                                   private val testsCacheInitializer: TestsCacheInitializer) {

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

        reinitializeCaches()
    }

    fun checkUpdateCompatibility(composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return composedStepUpdateCompatibilityFrontendService.checkUpdateCompatibility(composedStepDef)
    }

    fun saveComposedStep(composedStepDef: ComposedStepDef): ComposedStepDef {
        return saveFrontendService.saveComposedStep(composedStepDef)
    }

    fun renameComposedStepDirectory(renamePath: RenamePath): Path {
        val result = stepsCache.renameComposedStepDirectory(renamePath)

        reinitializeCaches()

        return result
    }

    fun deleteComposedStepDirectory(path: Path) {
        stepsCache.deleteComposedStepDirectory(path)

        reinitializeCaches()
    }

    fun moveComposedStepDirectoryOrFile(copyPath: CopyPath) {
        stepsCache.moveComposedStepDirectoryOrFile(copyPath)

        reinitializeCaches()
    }

    fun getDirectoriesTree(): ComposedContainerStepNode = stepsCache.getComposedStepsDirectoriesTree()

    fun getWarnings(composedStep: ComposedStepDef): ComposedStepDef {
        return warningService.composedStepWithWarnings(composedStep)
    }

    private fun reinitializeCaches() {
        // re-loading steps & tests to make sure tests are resolved properly
        // to optimize, we could re-load only the affected tests and/or steps
        stepsCacheInitializer.reinitializeComposedSteps()
        testsCacheInitializer.initialize()
    }

}
