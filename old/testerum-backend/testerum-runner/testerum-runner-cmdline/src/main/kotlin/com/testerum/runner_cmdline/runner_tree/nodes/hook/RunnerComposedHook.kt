package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class RunnerComposedHook(
    override val parent: RunnerTreeNode,
    private val phase: HookPhase,
    override val source: HookSource,
) : RunnerHook, RunnerTreeNode(), TreeNode {

    override val id: String = parent.id

    private lateinit var step: RunnerStep

    fun setStep(step: RunnerStep) {
        this.step = step
    }

    override fun execute(context: RunnerContext): ExecutionStatus {
        logComposedHookStart(context)

        var executionStatus = ExecutionStatus.PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            val nestedStatus: ExecutionStatus = step.execute(context)

            if (nestedStatus > executionStatus) {
                executionStatus = nestedStatus
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logComposedHookEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    override fun skip(context: RunnerContext) {
        step.skip(context)
    }

    private fun logComposedHookStart(context: RunnerContext) {
        context.logMessage("Started executing composed hook [$phase $step (source=$source)]")
    }

    private fun logComposedHookEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage(
            "Finished executing composed hook [$phase $step (source=$source)]; duration=$durationMillis ms, status: [$executionStatus]",
            exception
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("composed-hook ").append(phase)
        destination.append(" (source=").append(source).append(") ")
        step.addToString(destination, 0)
    }
}
