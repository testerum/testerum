package com.testerum.runner_cmdline.runner_tree.nodes.step

import com.testerum.model.step.StepCall
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

abstract class RunnerStep(
    parent: TreeNode,
    val stepCall: StepCall,
    indexInParent: Int,
    private val logEvents: Boolean // todo: always log events, after we implement composed hooks in the frontend tree
) : RunnerTreeNode(), TreeNode {

    override val parent: RunnerTreeNode = parent as? RunnerTreeNode
        ?: throw IllegalArgumentException("unexpected parent note type [${parent.javaClass}]: [$parent]")

    override val positionInParent = PositionInParent(stepCall.id, indexInParent)

    protected abstract fun doRun(context: RunnerContext): ExecutionStatus
    protected open fun doSkip(context: RunnerContext) {}
    protected open fun doDisable(context: RunnerContext) {}

    fun execute(context: RunnerContext): ExecutionStatus {
        if (!stepCall.enabled) {
            return disable(context)
        }

        logStepStart(context)

        var executionStatus = ExecutionStatus.PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            executionStatus = doRun(context)
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logStepEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    fun skip(context: RunnerContext) {
        logStepStart(context)

        var executionStatus = ExecutionStatus.SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            doSkip(context)
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logStepEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }
    }

    fun disable(context: RunnerContext): ExecutionStatus {
        logStepStart(context)

        var executionStatus = ExecutionStatus.DISABLED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            doDisable(context)
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logStepEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    private fun logStepStart(context: RunnerContext) {
        if (logEvents) {
            context.logEvent(
                StepStartEvent(eventKey = eventKey, stepCall = stepCall)
            )
        }
        context.logMessage("Started executing step [$stepCall]")
    }

    private fun logStepEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage("Finished executing step [$stepCall]; status: [$executionStatus]", exception)
        if (logEvents) {
            context.logEvent(
                StepEndEvent(
                    eventKey = eventKey,
                    stepCall = stepCall,
                    status = executionStatus,
                    durationMillis = durationMillis
                )
            )
        }
    }

}

