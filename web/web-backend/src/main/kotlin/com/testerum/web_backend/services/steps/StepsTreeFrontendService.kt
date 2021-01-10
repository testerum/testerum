package com.testerum.web_backend.services.steps

import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.tree.RootStepNode
import com.testerum.model.step.tree.builder.StepTreeBuilder
import com.testerum.web_backend.services.project.WebProjectManager

class StepsTreeFrontendService(private val webProjectManager: WebProjectManager) {

    fun getStepTree(filter: StepsTreeFilter): RootStepNode {
        val treeBuilder = StepTreeBuilder()

        val stepsCache = webProjectManager.getProjectServices().getStepsCache()
        val basicSteps = stepsCache.getBasicSteps()
        val composedStep = stepsCache.getComposedSteps()

        return treeBuilder.build(basicSteps, composedStep)
    }

}
