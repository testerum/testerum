package com.testerum.web_backend.services.steps

import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.tree.RootStepNode
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.services.steps.builder.StepTreeBuilder

class StepsTreeFrontendService(private val webProjectManager: WebProjectManager,
                               private val composedStepUpdateCompatibilityFrontendService: ComposedStepUpdateCompatibilityFrontendService) {

    fun getStepTree(filter: StepsTreeFilter): RootStepNode {
        val treeBuilder = StepTreeBuilder(composedStepUpdateCompatibilityFrontendService)

        val stepsCache = webProjectManager.getProjectServices().getStepsCache()
        val basicSteps = stepsCache.getBasicSteps()
        val composedStep = stepsCache.getComposedSteps()

        return treeBuilder.build(basicSteps, composedStep)
    }
}
