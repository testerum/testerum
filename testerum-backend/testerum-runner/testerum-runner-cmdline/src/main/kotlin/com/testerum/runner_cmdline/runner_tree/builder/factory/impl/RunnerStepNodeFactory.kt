package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerUndefinedStep

object RunnerStepNodeFactory {

    fun create(
        parentNode: TreeNode,
        indexInParent: Int,
        stepCall: StepCall,
        logEvents: Boolean,
    ): RunnerStep {
        return when (val stepDef = stepCall.stepDef) {
            is UndefinedStepDef -> RunnerUndefinedStep(parentNode, stepCall, indexInParent, logEvents)
            is BasicStepDef -> RunnerBasicStep(parentNode, stepCall, indexInParent, logEvents)
            is ComposedStepDef -> {
                val runnerComposedStep = RunnerComposedStep(
                    parent = parentNode,
                    stepCall = stepCall,
                    indexInParent = indexInParent,
                    logEvents = logEvents
                )

                for ((nestedIndexInParent, nestedStepCall) in stepDef.stepCalls.withIndex()) {
                    runnerComposedStep.addChild(
                        create(runnerComposedStep, nestedIndexInParent, nestedStepCall, logEvents)
                    )
                }

                runnerComposedStep
            }
            else -> throw RuntimeException("unknown StepDef type [${stepDef.javaClass.name}]")
        }
    }
}
