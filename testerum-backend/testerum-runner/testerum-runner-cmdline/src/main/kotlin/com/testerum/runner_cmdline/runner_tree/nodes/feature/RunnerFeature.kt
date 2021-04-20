package com.testerum.runner_cmdline.runner_tree.nodes.feature

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.id.RunnerIdCreator
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooks
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.FAILED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.SKIPPED

class RunnerFeature(
    override val parent: RunnerTreeNode,
    path: Path,
    val featureName: String,
    val tags: List<String>,
    val feature: Feature
) : RunnerFeatureOrTest(), ContainerTreeNode {

    override val id: String = RunnerIdCreator.getFeatureId(parent.id, path)

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

    override fun execute(context: RunnerContext): ExecutionStatus {
        try {
            return tryToRun(context)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute feature [$featureName]", e)
        }
    }

    private fun tryToRun(context: RunnerContext): ExecutionStatus {
        logFeatureStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        context.variablesContext.startFeature()
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
            context.variablesContext.endFeature()

            logFeatureEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
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

    override fun skip(context: RunnerContext): ExecutionStatus {
        logFeatureStart(context)

        var status = SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (featureOrTest in children) {
                val featureOrTestStatus: ExecutionStatus = featureOrTest.skip(context)

                if (featureOrTestStatus > status) {
                    status = featureOrTestStatus
                }
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            logFeatureEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
    }

    private fun logFeatureStart(context: RunnerContext) {
        context.logEvent(
            FeatureStartEvent(
                eventKey = eventKey,
                featureName = featureName,
                tags = tags
            )
        )
        context.logMessage("Started executing feature [$featureName]")
    }

    private fun logFeatureEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage("Finished executing feature [$featureName]; status: [$executionStatus]", exception)
        context.logEvent(
            FeatureEndEvent(
                eventKey = eventKey,
                featureName = featureName,
                status = executionStatus,
                durationMillis = durationMillis
            )
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("feature '").append(featureName).append("'\n")

        beforeHooks.addToString(destination, indentLevel + 1)

        for (featureOrTest in children) {
            featureOrTest.addToString(destination, indentLevel + 1)
        }

        afterHooks.addToString(destination, indentLevel + 1)
    }
}
