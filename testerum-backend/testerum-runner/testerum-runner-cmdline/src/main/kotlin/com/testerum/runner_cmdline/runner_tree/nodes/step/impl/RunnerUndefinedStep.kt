package com.testerum.runner_cmdline.runner_tree.nodes.step.impl

import com.testerum.common_kotlin.indent
import com.testerum.model.step.StepCall
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.UNDEFINED

class RunnerUndefinedStep(
    parent: TreeNode,
    stepCall: StepCall,
    indexInParent: Int,
    logEvents: Boolean,
) : RunnerStep(parent, stepCall, indexInParent, logEvents) {

    override fun doRun(context: RunnerContext) = UNDEFINED

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("step ")
        stepCall.toString(destination, 0)
        destination.append("\n")
    }

}
