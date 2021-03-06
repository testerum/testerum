package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.common_kotlin.indent
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class RunnerBeforeHooksList(
    private val hooks: List<RunnerHook>,
) {

    fun execute(context: RunnerContext): ExecutionStatus {
        if (hooks.isEmpty()) {
            return ExecutionStatus.PASSED
        }

        var status = ExecutionStatus.PASSED

        for (hook in hooks) {
            if (status == ExecutionStatus.PASSED) {
                val hookStatus: ExecutionStatus = hook.execute(context)

                if (hookStatus > status) {
                    status = hookStatus
                }
            } else {
                hook.skip(context)
            }
        }

        return status
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    fun addToString(destination: StringBuilder, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.indent(indentLevel).append("before-hooks\n")

        for (hook in hooks) {
            hook.addToString(destination, indentLevel + 1)
        }
    }

}
