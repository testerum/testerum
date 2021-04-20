package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.runner.events.model.HooksEndEvent
import com.testerum.runner.events.model.HooksStartEvent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class RunnerBeforeHooks(
    override val parent: RunnerTreeNode,
    val hookPhase: HookPhase,
): RunnerTreeNode() {

    override val id: String = "${parent.id}/$hookPhase"
    lateinit var hooks: List<RunnerHook>

    fun execute(context: RunnerContext): ExecutionStatus {
        logHooksStart(context)

        if (hooks.isEmpty()) {
            return ExecutionStatus.PASSED
        }

        var status = ExecutionStatus.PASSED
        val startTime = System.currentTimeMillis()

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

        logHooksEnd(context, status, startTime)

        return status
    }

    private fun logHooksStart(context: RunnerContext) {
        context.logEvent(
            HooksStartEvent(
                eventKey = eventKey,
                hookPhase = hookPhase
            )
        )
    }

    private fun logHooksEnd(
        context: RunnerContext,
        status: ExecutionStatus,
        startTime: Long
    ) {
        context.logEvent(
            HooksEndEvent(
                eventKey = eventKey,
                hookPhase = hookPhase,
                status = status,
                durationMillis = System.currentTimeMillis() - startTime
            )
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.indent(indentLevel).append("before-hooks\n")

        for (hook in hooks) {
            hook.addToString(destination, indentLevel + 1)
        }
    }
}
