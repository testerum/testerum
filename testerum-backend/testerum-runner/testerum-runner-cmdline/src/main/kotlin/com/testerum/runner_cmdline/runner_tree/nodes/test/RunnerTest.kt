package com.testerum.runner_cmdline.runner_tree.nodes.test

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.model.test.TestModel
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.DynamicVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

data class RunnerTest(private val beforeEachTestHooks: List<RunnerHook>,
                      private val test: TestModel,
                      private val filePath: JavaPath,
                      private val indexInParent: Int,
                      private val steps: List<RunnerStep>,
                      private val afterEachTestHooks: List<RunnerHook>) : RunnerFeatureOrTest() {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(RunnerHook::class.java)
    }

    init {
        for (step in steps) {
            step.parent = this
        }
    }

    override lateinit var parent: RunnerTreeNode
    override val positionInParent = PositionInParent(test.id, indexInParent)

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> {
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

    override fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute test at [${filePath.toAbsolutePath().normalize()}]", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        if (test.properties.isDisabled) {
            return disable(context)
        }

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

                LOG.error(errorMessage, e)

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

                LOG.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            if (executionStatus == ExecutionStatus.PASSED && endHookStatus != ExecutionStatus.PASSED) {
                executionStatus = endHookStatus
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
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

    override fun skip(context: RunnerContext): ExecutionStatus {
        logTestStart(context)

        var executionStatus = ExecutionStatus.SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (step in steps) {
                step.skip(context)
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logTestEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
            return executionStatus
        }
    }

    private fun disable(context: RunnerContext): ExecutionStatus {
        logTestStart(context)

        var executionStatus = ExecutionStatus.DISABLED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (step in steps) {
                step.disable(context)
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logTestEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
            return executionStatus
        }
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

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        // show test info
        destination.indent(indentLevel).append("test")
        if (test.properties.isDisabled) {
            destination.append(" DISABLED")
        }
        destination.append(" '").append(test.text).append("'")
        if (test.tags.isNotEmpty()) {
            destination.append(", tags=").append(test.tags)
        }
        destination.append("\n")

        // show children
        for (beforeEachTestHook in beforeEachTestHooks) {
            beforeEachTestHook.addToString(destination, indentLevel + 1)
        }

        for (step in steps) {
            step.addToString(destination, indentLevel + 1)
        }

        for (afterEachTestHook in afterEachTestHooks) {
            afterEachTestHook.addToString(destination, indentLevel + 1)
        }
    }
}
