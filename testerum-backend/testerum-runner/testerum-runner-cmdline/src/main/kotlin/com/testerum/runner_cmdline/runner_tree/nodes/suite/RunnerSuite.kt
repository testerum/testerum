package com.testerum.runner_cmdline.runner_tree.nodes.suite

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.ExecutionStatus.PASSED
import com.testerum.common_kotlin.indent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext

data class RunnerSuite(private val beforeAllTestsHooks: List<RunnerHook>,
                       private val featuresOrTests: List<RunnerFeatureOrTest>,
                       private val afterAllTestsHooks: List<RunnerHook>,
                       private val executionName: String?) : RunnerTreeNode() {

    init {
        for (featureOrTest in featuresOrTests) {
            featureOrTest.parent = this
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
        for (featureOrTest in featuresOrTests) {
            glueClasses += featureOrTest.getGlueClasses(context)
        }
        for (hook in afterAllTestsHooks) {
            glueClasses += hook.getGlueClass(context)
        }

        return glueClasses
    }

    fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logSuiteStart(context)

        var suiteStatus: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            // run before all hooks
            suiteStatus = runHooks(context, beforeAllTestsHooks)

            // run tests
            if (suiteStatus == PASSED) {
                suiteStatus = runTests(context, globalVars, suiteStatus)
            } else {
                skipFeaturesOrTests(context)
            }

            // run after all hooks
            val afterAllHooksStatus = runHooks(context, afterAllTestsHooks)
            if (suiteStatus == PASSED && afterAllHooksStatus != PASSED) {
                suiteStatus = afterAllHooksStatus
            }
        } catch (e: Exception) {
            suiteStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            val durationMillis = System.currentTimeMillis() - startTime

            logSuiteEnd(context, suiteStatus, exception, durationMillis)
        }

        return suiteStatus
    }

    private fun runHooks(context: RunnerContext, hooks: List<RunnerHook>): ExecutionStatus {
        var status = PASSED

        for (hook in hooks) {
            if (status == PASSED) {
                val hookStatus: ExecutionStatus = hook.run(context)

                if (status == PASSED && hookStatus != PASSED) {
                    status = hookStatus
                }
            } else {
                hook.skip()
            }
        }

        return status
    }

    private fun runTests(context: RunnerContext,
                         globalVars: GlobalVariablesContext,
                         suiteExecutionStatus: ExecutionStatus): ExecutionStatus {
        var status = suiteExecutionStatus

        for (featureOrTest in featuresOrTests) {
            val featureOrTestStatus: ExecutionStatus = featureOrTest.run(context, globalVars)

            if (status == PASSED && featureOrTestStatus != PASSED) {
                status = featureOrTestStatus
            }
        }

        return status
    }

    private fun skipFeaturesOrTests(context: RunnerContext) {
        for (featureOrTest in featuresOrTests) {
            featureOrTest.skip(context)
        }
    }

    private fun logSuiteStart(context: RunnerContext) {
        context.logEvent(
                SuiteStartEvent(executionName = executionName)
        )
        context.logMessage("Started executing test suite")
    }

    private fun logSuiteEnd(context: RunnerContext,
                            executionStatus: ExecutionStatus,
                            exception: Throwable?,
                            durationMillis: Long) {
        context.logMessage("Finished executing test suite; status: [$executionStatus]", exception)
        context.logEvent(
                SuiteEndEvent(
                        status = executionStatus,
                        durationMillis = durationMillis
                )
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("Suite\n")

        for (beforeAllTestsHook in beforeAllTestsHooks) {
            beforeAllTestsHook.addToString(destination, indentLevel + 1)
        }

        for (test in featuresOrTests) {
            test.addToString(destination, indentLevel + 1)
        }

        for (afterAllTestsHook in afterAllTestsHooks) {
            afterAllTestsHook.addToString(destination, indentLevel + 1)
        }
    }

}
