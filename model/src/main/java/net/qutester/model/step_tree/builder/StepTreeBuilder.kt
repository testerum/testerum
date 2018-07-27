package net.qutester.model.step_tree.builder

import net.qutester.model.step.BasicStepDef
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step_tree.RootStepNode

class StepTreeBuilder {

    private val basicStepTreeBuilder = BasicStepTreeBuilder()
    private val composedStepTreeBuilder = ComposedStepTreeBuilder()

    fun addBasicStepDef(basicStepDef: BasicStepDef) {
        basicStepTreeBuilder.addBasicStepDef(basicStepDef)
    }

    fun addComposedStepDef(composedStepDef: ComposedStepDef) {
        composedStepTreeBuilder.addComposedStepDef(composedStepDef)
    }

    fun build(): RootStepNode {
        val basicStepsRoot = basicStepTreeBuilder.build()
        val composedStepsRoot = composedStepTreeBuilder.build()

        return RootStepNode(
                name = "Steps",
                basicStepsRoot = basicStepsRoot,
                composedStepsRoot = composedStepsRoot,
                hasOwnOrDescendantWarnings = basicStepsRoot.hasOwnOrDescendantWarnings || composedStepsRoot.hasOwnOrDescendantWarnings
        )
    }

}