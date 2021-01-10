package com.testerum.model.step.tree.builder

import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.tree.RootStepNode

class StepTreeBuilder {

    private val basicStepTreeBuilder = BasicStepTreeBuilder()
    private val composedStepTreeBuilder = ComposedStepTreeBuilder()

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
