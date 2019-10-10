package com.testerum.runner_cmdline.runner_tree.nodes

import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext

abstract class RunnerFeatureOrTest : RunnerTreeNode() {

    override lateinit var parent: RunnerTreeNode

    abstract fun getGlueClasses(context: RunnerContext): List<Class<*>>

    abstract fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus
    abstract fun skip(context: RunnerContext): ExecutionStatus

}
