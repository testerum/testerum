package com.testerum.web_backend.services.steps.builder

import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.tree.RootStepNode
import com.testerum.web_backend.services.steps.ComposedStepUpdateCompatibilityFrontendService

class StepTreeBuilder(private val composedStepUpdateCompatibilityFrontendService: ComposedStepUpdateCompatibilityFrontendService) {

    private val basicStepTreeBuilder = BasicStepTreeBuilder()
    private val composedStepTreeBuilder = ComposedStepTreeBuilder(composedStepUpdateCompatibilityFrontendService)

    fun build(basicSteps: List<BasicStepDef>, composedStep: List<ComposedStepDef>): RootStepNode {
        val basicStepsRoot = basicStepTreeBuilder.createTree(basicSteps)
        val composedStepsRoot = composedStepTreeBuilder.createTree(composedStep)

        return RootStepNode(
                name = "Steps",
                basicStepsRoot = basicStepsRoot,
                composedStepsRoot = composedStepsRoot,
                hasOwnOrDescendantWarnings = basicStepsRoot.hasOwnOrDescendantWarnings || composedStepsRoot.hasOwnOrDescendantWarnings
        )
    }

}
