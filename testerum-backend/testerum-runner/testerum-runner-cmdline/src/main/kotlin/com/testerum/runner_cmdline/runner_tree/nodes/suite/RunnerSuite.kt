package com.testerum.runner_cmdline.runner_tree.nodes.suite

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner.events.model.statistics.ExecutionStatistics
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class RunnerSuite(private val beforeAllTestsHooks: List<RunnerHook>,
                       private val tests: List<RunnerTest>,
                       private val afterAllTestsHooks: List<RunnerHook>) : RunnerTreeNode() {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RunnerHook::class.java)
    }

    init {
        for (test in tests) {
            test.parent = this
        }
    }

    override val parent: RunnerTreeNode? = null

    override val positionInParent: PositionInParent
        get() = EventKey.SUITE_POSITION_IN_PARENT

    fun addClassesToGlueObjectFactory(context: RunnerContext) {
        for (glueClass in getGlueClasses(context)) {
            context.glueObjectFactory.addClass(glueClass)
        }
    }

    private fun getGlueClasses(context: RunnerContext): List<Class<*>> {
        val glueClasses = mutableListOf<Class<*>>()

        for (hook in beforeAllTestsHooks) {
            glueClasses += hook.getGlueClass(context)
        }
        for (test in tests) {
            glueClasses += test.getGlueClasses(context)
        }
        for (hook in afterAllTestsHooks) {
            glueClasses += hook.getGlueClass(context)
        }

        return glueClasses
    }

    fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logSuiteStart(context)

        var suiteExecutionStatus: ExecutionStatus = ExecutionStatus.PASSED
        val startTime = System.currentTimeMillis()
        var successfulTestsCount = 0
        try {
            for (hook in beforeAllTestsHooks) {
                if (suiteExecutionStatus == ExecutionStatus.PASSED) {
                    val stepExecutionStatus: ExecutionStatus = hook.run(context)

                    suiteExecutionStatus = stepExecutionStatus
                } else {
                    hook.skip()
                }
            }

            try {
                for (test in tests) {
                    val testExecutionStatus: ExecutionStatus = test.run(context, globalVars)

                    if (testExecutionStatus == ExecutionStatus.PASSED) {
                        successfulTestsCount++
                    }

                    if (suiteExecutionStatus == ExecutionStatus.PASSED
                            && testExecutionStatus != ExecutionStatus.PASSED) {
                        suiteExecutionStatus = testExecutionStatus
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "failed to execute ${HookPhase.BEFORE_ALL_TESTS} hooks"

                LOGGER.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            var endHookStatus: ExecutionStatus = ExecutionStatus.PASSED
            try {
                for (hook in afterAllTestsHooks) {
                    if (endHookStatus == ExecutionStatus.PASSED) {
                        val stepExecutionStatus: ExecutionStatus = hook.run(context)

                        endHookStatus = stepExecutionStatus
                    } else {
                        hook.skip()
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "failed to execute ${HookPhase.AFTER_ALL_TESTS} hooks"

                LOGGER.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            if (suiteExecutionStatus == ExecutionStatus.PASSED && endHookStatus != ExecutionStatus.PASSED) {
                suiteExecutionStatus = endHookStatus
            }
        } finally {
            val durationMillis = System.currentTimeMillis() - startTime

            logSuiteEnd(context, suiteExecutionStatus, successfulTestsCount, durationMillis)
        }

        return suiteExecutionStatus
    }

    private fun logSuiteEnd(context: RunnerContext, executionStatus: ExecutionStatus, successfulTestsCount: Int, durationMillis: Long) {
        context.eventsService.logEvent(
                SuiteEndEvent(
                        status = executionStatus,
                        statistics = ExecutionStatistics(tests.size - successfulTestsCount, successfulTestsCount, tests.size), // todo: return count per execution status instead of this?
                        durationMillis = durationMillis
                )
        )
    }

    private fun logSuiteStart(context: RunnerContext) {
        context.eventsService.logEvent(
                SuiteStartEvent()
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("Suite\n")

        for (beforeAllTestsHook in beforeAllTestsHooks) {
            beforeAllTestsHook.addToString(destination, indentLevel + 1)
        }

        for (test in tests) {
            test.addToString(destination, indentLevel + 1)
        }

        for (afterAllTestsHook in afterAllTestsHooks) {
            afterAllTestsHook.addToString(destination, indentLevel + 1)
        }
    }

}
