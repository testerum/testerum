package com.testerum.runner.runner_tree.nodes.test

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.model.test.TestModel
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner.runner_tree.nodes.step.RunnerStep
import com.testerum.runner.runner_tree.runner_context.RunnerContext
import com.testerum.runner.runner_tree.vars_context.DynamicVariablesContext
import com.testerum.runner.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner.runner_tree.vars_context.VariablesContext
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class RunnerTest(private val beforeEachTestHooks: List<RunnerHook>,
                      private val test: TestModel,
                      private val filePath: java.nio.file.Path,
                      private val indexInParent: Int,
                      private val steps: List<RunnerStep>,
                      private val afterEachTestHooks: List<RunnerHook>) : RunnerTreeNode() {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RunnerHook::class.java)
    }

    init {
        for (step in steps) {
            step.parent = this
        }
    }

    override lateinit var parent: RunnerTreeNode
    override val positionInParent = PositionInParent(test.id, indexInParent)

    fun getGlueClasses(context: RunnerContext): List<Class<*>> {
        val glueClasses = mutableListOf<Class<*>>()

        for (hook in beforeEachTestHooks) {
            glueClasses += hook.getGlueClass(context)
        }
        for (step in steps) {
            glueClasses.addAll(
                    step.getGlueClasses(context)
            )
        }
        for (hook in afterEachTestHooks) {
            glueClasses += hook.getGlueClass(context)
        }

        return glueClasses
    }

    fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute test at [${filePath.toAbsolutePath().normalize()}]", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logTestStart(context)

        var executionStatus: ExecutionStatus = ExecutionStatus.PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            val dynamicVars = DynamicVariablesContext()
            context.glueObjectFactory.beforeTest()

            val vars = VariablesContext.forTest(dynamicVars, globalVars)
            context.testVariables.setVariablesContext(vars)

            try {
                for (hook in beforeEachTestHooks) {
                    if (executionStatus == ExecutionStatus.PASSED) {
                        val stepExecutionStatus: ExecutionStatus = hook.run(context)

                        executionStatus = stepExecutionStatus
                    } else {
                        hook.skip()
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "failed to execute ${HookPhase.BEFORE_EACH_TEST} hooks"

                LOGGER.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            for (step in steps) {
                if (executionStatus == ExecutionStatus.PASSED) {
                    val stepExecutionStatus: ExecutionStatus = step.run(context, vars)

                    executionStatus = stepExecutionStatus
                } else {
                    step.skip(context)
                }
            }

            var endHookStatus: ExecutionStatus = ExecutionStatus.PASSED
            try {
                for (hook in afterEachTestHooks) {
                    if (endHookStatus == ExecutionStatus.PASSED) {
                        val stepExecutionStatus: ExecutionStatus = hook.run(context)

                        endHookStatus = stepExecutionStatus
                    } else {
                        hook.skip()
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "failed to execute ${HookPhase.AFTER_EACH_TEST} hooks"

                LOGGER.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            if (executionStatus == ExecutionStatus.PASSED && endHookStatus != ExecutionStatus.PASSED) {
                executionStatus = endHookStatus
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.ERROR
            exception = e
        } finally {
            try {
                context.glueObjectFactory.afterTest()
            } catch (e: Exception) {
                val afterTestException = RuntimeException("glueObjectFactory.afterTest() failed", e)

                exception = RunnerTestException(
                        message = "test execution failure",
                        cause = exception,
                        suppressedException = afterTestException
                )
            }

            logTestEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    private fun logTestStart(context: RunnerContext) {
        context.eventsService.logEvent(
                TestStartEvent(
                        eventKey = eventKey,
                        testName = test.text,
                        testFilePath = test.path
                )
        )
    }

    private fun logTestEnd(context: RunnerContext,
                           executionStatus: ExecutionStatus,
                           exception: Throwable?,
                           durationMillis: Long) {
        context.eventsService.logEvent(
                TestEndEvent(
                        eventKey = eventKey,
                        testFilePath = test.path,
                        testName = test.text,
                        status = executionStatus,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) },
                        durationMillis = durationMillis
                )
        )
    }

    override fun toString() = test.toString()

}
