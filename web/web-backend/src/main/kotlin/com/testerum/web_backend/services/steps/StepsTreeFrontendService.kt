package com.testerum.web_backend.services.steps

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.tree.RootStepNode
import com.testerum.model.step.tree.builder.StepTreeBuilder
import com.testerum.web_backend.services.steps.filterer.StepsTreeFilterer

class StepsTreeFrontendService(private val stepsCache: StepsCache) {

    fun getStepTree(filter: StepsTreeFilter): RootStepNode {
        val treeBuilder = StepTreeBuilder()

        val steps = stepsCache.getAllSteps()

        for (step in steps) {
            if (StepsTreeFilterer.matches(step, filter)) {
                when (step) {
                    is BasicStepDef -> treeBuilder.addBasicStepDef(step)
                    is ComposedStepDef -> treeBuilder.addComposedStepDef(step)
                }
            }
        }

        return treeBuilder.build()
    }

}
