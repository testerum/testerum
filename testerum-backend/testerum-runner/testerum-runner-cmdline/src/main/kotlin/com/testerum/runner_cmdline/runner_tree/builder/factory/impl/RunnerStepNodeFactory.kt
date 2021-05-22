package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerUndefinedStep

object RunnerStepNodeFactory {

    fun create(
        parentNode: RunnerTreeNode,
        indexInParent: Int,
        stepCall: StepCall
    ): RunnerStep {
        return when (val stepDef = stepCall.stepDef) {
            is UndefinedStepDef -> RunnerUndefinedStep(parentNode, stepCall, indexInParent)
            is BasicStepDef -> RunnerBasicStep(parentNode, stepCall, indexInParent)
            is ComposedStepDef -> {
                val runnerComposedStep = RunnerComposedStep(
                    parent = parentNode,
                    stepCall = stepCall,
                    indexInParent = indexInParent
                )

                for ((nestedIndexInParent, nestedStepCall) in stepDef.stepCalls.withIndex()) {
                    runnerComposedStep.addChild(
                        create(runnerComposedStep, nestedIndexInParent, nestedStepCall)
                    )
                }

                runnerComposedStep
            }
            else -> throw RuntimeException("unknown StepDef type [${stepDef.javaClass.name}]")
        }
    }
}
