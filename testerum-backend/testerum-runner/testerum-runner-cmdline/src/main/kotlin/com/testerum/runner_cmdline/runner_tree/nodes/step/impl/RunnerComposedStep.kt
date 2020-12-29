package com.testerum.runner_cmdline.runner_tree.nodes.step.impl

import com.testerum.common_kotlin.indent
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.UNDEFINED

class RunnerComposedStep(
    parent: TreeNode,
    stepCall: StepCall,
    indexInParent: Int,
    logEvents: Boolean,
) : RunnerStep(parent, stepCall, indexInParent, logEvents) {

    private val steps = mutableListOf<RunnerStep>()

    fun addChild(step: RunnerStep) {
        this.steps += step
    }

    override fun doRun(context: RunnerContext): ExecutionStatus {
        if (steps.isEmpty()) {
            val executionStatus = UNDEFINED
            context.logMessage("marking composed step [${(stepCall.stepDef as ComposedStepDef).path}] as $executionStatus because it doesn't have any child steps")

            return executionStatus
        }

        var status: ExecutionStatus = PASSED

        context.variablesContext.startComposedStep()
        for (step in steps) {
            if (status <= PASSED) {
                val nestedStatus: ExecutionStatus = step.execute(context)

                if (nestedStatus > status) {
                    status = nestedStatus
                }
            } else {
                step.skip(context)
            }
        }
        context.variablesContext.endComposedStep()

        return status
    }

    override fun doSkip(context: RunnerContext) {
        for (step in steps) {
            step.skip(context)
        }
    }

    override fun doDisable(context: RunnerContext) {
        for (step in steps) {
            step.disable(context)
        }
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("step ")
        stepCall.toString(destination, 0)
        destination.append("\n")

        for (step in steps) {
            step.addToString(destination, indentLevel + 1)
        }
    }

}
