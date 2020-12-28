package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.common_kotlin.indent
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.DynamicVarsContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVarsContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class RunnerAfterHooksList(
    private val hooks: List<RunnerHook>,
) {

    fun run(context: RunnerContext, globalVars: GlobalVarsContext): ExecutionStatus {
        if (hooks.isEmpty()) {
            return ExecutionStatus.PASSED
        }

        var status = ExecutionStatus.PASSED

        val dynamicVars = DynamicVarsContext()
        val vars = VariablesContext.forTest(dynamicVars, globalVars)

        for (hook in hooks) {
            val hookStatus: ExecutionStatus = hook.run(context, vars)

            if (hookStatus > status) {
                status = hookStatus
            }
        }

        return status
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("after-hooks\n")

        for (hook in hooks) {
            hook.addToString(destination, indentLevel + 1)
        }
    }

}
