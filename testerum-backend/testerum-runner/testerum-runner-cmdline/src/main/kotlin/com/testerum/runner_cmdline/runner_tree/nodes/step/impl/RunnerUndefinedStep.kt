package com.testerum.runner_cmdline.runner_tree.nodes.step.impl

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.model.step.StepCall
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext

class RunnerUndefinedStep(stepCall: StepCall,
                          indexInParent: Int) : RunnerStep(stepCall, indexInParent) {

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> = emptyList()

    override fun doRun(context: RunnerContext, vars: VariablesContext) = ExecutionStatus.UNDEFINED

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("step ")
        stepCall.toString(destination, 0)
        destination.append("\n")
    }

}
