package com.testerum.runner.runner_tree.nodes.step.impl

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.model.step.StepCall
import com.testerum.runner.runner_tree.nodes.step.RunnerStep
import com.testerum.runner.runner_tree.runner_context.RunnerContext
import com.testerum.runner.runner_tree.vars_context.VariablesContext

class RunnerUndefinedStep(stepCall: StepCall,
                          indexInParent: Int) : RunnerStep(stepCall, indexInParent) {

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> = emptyList()

    override fun doRun(context: RunnerContext, vars: VariablesContext) = ExecutionStatus.UNDEFINED

}
