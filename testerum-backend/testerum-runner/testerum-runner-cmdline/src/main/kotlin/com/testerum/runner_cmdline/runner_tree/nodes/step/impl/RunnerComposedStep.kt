package com.testerum.runner_cmdline.runner_tree.nodes.step.impl

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.model.step.StepCall
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext

class RunnerComposedStep(stepCall: StepCall,
                         indexInParent: Int,
                         val steps: List<RunnerStep>) : RunnerStep(stepCall, indexInParent) {

    init {
        for (step in steps) {
            step.parent = this
        }
    }

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> {
        val glueClasses = mutableListOf<Class<*>>()

        for (step in steps) {
            glueClasses.addAll(
                    step.getGlueClasses(context)
            )
        }

        return glueClasses
    }

    override fun doRun(context: RunnerContext, vars: VariablesContext): ExecutionStatus {
        var executionStatus: ExecutionStatus = ExecutionStatus.PASSED

        val subVars = vars.forStep(stepCall)
        context.testVariables.setVariablesContext(subVars)

        for (step in steps) {
            if (executionStatus == ExecutionStatus.PASSED) {
                val nestedExecutionStatus: ExecutionStatus = step.run(context, subVars)

                executionStatus = nestedExecutionStatus
            } else {
                step.skip(context)
            }
        }

        return executionStatus
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
