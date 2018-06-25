package com.testerum.runner.runner_tree.nodes.step.impl

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.runner.runner_tree.nodes.step.RunnerStep
import com.testerum.runner.runner_tree.runner_context.RunnerContext
import com.testerum.runner.runner_tree.vars_context.VariablesContext
import net.qutester.model.step.StepCall

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

}
