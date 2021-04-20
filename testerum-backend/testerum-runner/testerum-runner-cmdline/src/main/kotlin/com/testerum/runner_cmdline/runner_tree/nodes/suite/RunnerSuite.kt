package com.testerum.runner_cmdline.runner_tree.nodes.suite

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.Feature
import com.testerum.model.runner.tree.id.RunnerIdCreator
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooks
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.FAILED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED

class RunnerSuite(
    val executionName: String?,
    val glueClassNames: List<String>,
    val feature: Feature
) : RunnerTreeNode(), ContainerTreeNode {

    override val id: String = RunnerIdCreator.getRootId()
    override val parent: RunnerTreeNode? = null

    private val children = mutableListOf<RunnerFeatureOrTest>()

    val featuresOrTests: List<RunnerFeatureOrTest>
        get() = children

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        children += child as? RunnerFeatureOrTest
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
    }

    private lateinit var beforeHooks: RunnerBeforeHooks
    private lateinit var afterHooks: RunnerAfterHooks

    fun setBeforeHooks(beforeHooks: RunnerBeforeHooks) {
        this.beforeHooks = beforeHooks
    }

    fun setAfterHooks(afterHooks: RunnerAfterHooks) {
        this.afterHooks = afterHooks
    }

    fun execute(context: RunnerContext): ExecutionStatus {
        logSuiteStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        context.variablesContext.startSuite()
        try {
            // before all hooks
            val beforeHooksStatus = beforeHooks.execute(context)
            status = beforeHooksStatus

            // children
            if (beforeHooksStatus == PASSED) {
                val childrenStatus = runChildren(context, status)
                if (childrenStatus > status) {
                    status = childrenStatus
                }
            } else {
                skipChildren(context)
            }

            // after all hooks
            val afterHooksStatus = afterHooks.execute(context, status)
            if (afterHooksStatus > status) {
                status = afterHooksStatus
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            context.variablesContext.endSuite()

            val durationMillis = System.currentTimeMillis() - startTime

            logSuiteEnd(context, status, exception, durationMillis)
        }

        return status
    }

    private fun runChildren(
        context: RunnerContext,
        overallStatus: ExecutionStatus,
    ): ExecutionStatus {
        var status = overallStatus

        for (child in children) {
            val childStatus: ExecutionStatus = child.execute(context)

            if (childStatus > status) {
                status = childStatus
            }
        }

        return status
    }

    private fun skipChildren(context: RunnerContext) {
        for (child in children) {
            child.skip(context)
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
