package com.testerum.runner_cmdline.runner_tree.nodes.suite

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.Feature
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooksList
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.DynamicVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.FAILED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED

class RunnerSuite(
    val executionName: String?,
    val glueClassNames: List<String>,
    val feature: Feature
) : RunnerTreeNode(), ContainerTreeNode {

    override val parent: RunnerTreeNode? = null

    override val positionInParent: PositionInParent
        get() = EventKey.SUITE_POSITION_IN_PARENT

    private val children = mutableListOf<RunnerFeatureOrTest>()

    val featuresOrTests: List<RunnerFeatureOrTest>
        get() = children

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        children += child as? RunnerFeatureOrTest
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
    }

    private lateinit var beforeHooks: RunnerBeforeHooksList
    private lateinit var afterHooks: RunnerAfterHooksList

    fun setBeforeHooks(beforeHooksList: RunnerBeforeHooksList) {
        this.beforeHooks = beforeHooksList
    }

    fun setAfterHooks(afterHooksList: RunnerAfterHooksList) {
        this.afterHooks = afterHooksList
    }

    fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logSuiteStart(context)

        var suiteStatus: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            val dynamicVars = DynamicVariablesContext()
            val vars = VariablesContext.forTest(dynamicVars, globalVars)
            context.testVariables.setVariablesContext(vars)

            // before all hooks
            suiteStatus = beforeHooks.run(context, globalVars)

            // children
            if (suiteStatus == PASSED) {
                val childrenStatus = runChildren(context, globalVars, suiteStatus)
                if (childrenStatus > suiteStatus) {
                    suiteStatus = childrenStatus
                }
            } else {
                skipFeaturesOrTests(context)
            }

            // after all hooks
            val afterHooksStatus = afterHooks.run(context, globalVars)
            if (afterHooksStatus > suiteStatus) {
                suiteStatus = afterHooksStatus
            }
        } catch (e: Exception) {
            suiteStatus = FAILED
            exception = e
        } finally {
            val durationMillis = System.currentTimeMillis() - startTime

            logSuiteEnd(context, suiteStatus, exception, durationMillis)
        }

        return suiteStatus
    }

    private fun runChildren(
        context: RunnerContext,
        globalVars: GlobalVariablesContext,
        suiteExecutionStatus: ExecutionStatus
    ): ExecutionStatus {
        var status = suiteExecutionStatus

        for (featureOrTest in children) {
            val featureOrTestStatus: ExecutionStatus = featureOrTest.run(context, globalVars)

            if (featureOrTestStatus > status) {
                status = featureOrTestStatus
            }
        }

        return status
    }

    private fun skipFeaturesOrTests(context: RunnerContext) {
        for (featureOrTest in children) {
            featureOrTest.skip(context)
        }
    }

    private fun logSuiteStart(context: RunnerContext) {
        context.logEvent(
            SuiteStartEvent(executionName = executionName)
        )
        context.logMessage("Started executing test suite")
    }

    private fun logSuiteEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
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

        beforeHooks.addToString(destination, indentLevel + 1)

        for (test in children) {
            test.addToString(destination, indentLevel + 1)
        }

        afterHooks.addToString(destination, indentLevel + 1)
    }

}
