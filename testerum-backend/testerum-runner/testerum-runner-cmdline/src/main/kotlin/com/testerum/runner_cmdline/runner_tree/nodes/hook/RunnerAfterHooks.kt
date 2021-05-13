package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.runner.events.model.HooksEndEvent
import com.testerum.runner.events.model.HooksStartEvent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class RunnerAfterHooks(
    override val parent: RunnerTreeNode,
    val hookPhase: HookPhase,
) : RunnerTreeNode() {

    override val id: String = "${parent.id}/$hookPhase"
    lateinit var hooks: List<RunnerHook>

    fun execute(
        context: RunnerContext,
        statusUntilNow: ExecutionStatus
    ): ExecutionStatus {
        if (hooks.isEmpty()) {
            return statusUntilNow
        }

        logHooksStart(context)

        var status = statusUntilNow
        val startTime = System.currentTimeMillis()

        for (hook in hooks) {
            context.testContext.testStatus = status
            val hookStatus: ExecutionStatus = hook.execute(context)

            if (hookStatus > status) {
                status = hookStatus
            }
        }

        logHooksEnd(context, status, startTime)

        return status
    }

    private fun logHooksStart(context: RunnerContext) {
        if (hasUserDefinedHooks()) {
            context.logEvent(
                HooksStartEvent(
                    eventKey = eventKey,
                    hookPhase = hookPhase
                )
            )
        }
    }

    private fun logHooksEnd(
        context: RunnerContext,
        status: ExecutionStatus,
        startTime: Long
    ) {
        if (hasUserDefinedHooks()) {
            context.logEvent(
                HooksEndEvent(
                    eventKey = eventKey,
                    hookPhase = hookPhase,
                    status = status,
                    durationMillis = System.currentTimeMillis() - startTime
                )
            )
        }
    }

    private fun hasUserDefinedHooks() = hooks.find { it !is RunnerBasicHook } != null

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        if (hooks.isEmpty()) {
            return
        }

        destination.indent(indentLevel).append("after-hooks\n")

        for (hook in hooks) {
            hook.addToString(destination, indentLevel + 1)
        }
    }
}
