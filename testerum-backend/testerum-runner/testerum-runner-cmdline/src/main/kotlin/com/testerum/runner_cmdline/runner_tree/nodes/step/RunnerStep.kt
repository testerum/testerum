package com.testerum.runner_cmdline.runner_tree.nodes.step

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.model.step.StepCall
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext

abstract class RunnerStep(val stepCall: StepCall,
                          indexInParent: Int) : RunnerTreeNode() {

    override lateinit var parent: RunnerTreeNode
    override val positionInParent = PositionInParent(stepCall.id, indexInParent)

    abstract fun getGlueClasses(context: RunnerContext): List<Class<*>>

    protected abstract fun doRun(context: RunnerContext, vars: VariablesContext): ExecutionStatus
    protected open fun doSkip(context: RunnerContext) { }
    protected open fun doDisable(context: RunnerContext) { }

    fun run(context: RunnerContext, vars: VariablesContext): ExecutionStatus {
        logStepStart(context)

        var executionStatus = ExecutionStatus.PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            executionStatus = doRun(context, vars)
        } catch (e: AssertionError) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
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

    fun disable(context: RunnerContext) {
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
    }

    private fun logStepStart(context: RunnerContext) {
        context.eventsService.logEvent(
                StepStartEvent(eventKey = eventKey, stepCall = stepCall)
        )
    }

    private fun logStepEnd(context: RunnerContext,
                           executionStatus: ExecutionStatus,
                           exception: Throwable?,
                           durationMillis: Long) {
        context.eventsService.logEvent(
                StepEndEvent(
                        eventKey = eventKey,
                        stepCall = stepCall,
                        status = executionStatus,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) },
                        durationMillis = durationMillis
                )
        )
    }

}

