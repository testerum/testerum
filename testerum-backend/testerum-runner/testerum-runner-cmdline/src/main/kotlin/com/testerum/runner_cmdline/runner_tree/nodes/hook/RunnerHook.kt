package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

interface RunnerHook {

    val source: HookSource

    fun execute(context: RunnerContext): ExecutionStatus

    fun skip(context: RunnerContext)

    fun addToString(destination: StringBuilder, indentLevel: Int)

}
