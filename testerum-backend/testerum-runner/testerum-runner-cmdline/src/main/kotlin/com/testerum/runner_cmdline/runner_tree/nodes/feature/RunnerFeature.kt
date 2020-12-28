package com.testerum.runner_cmdline.runner_tree.nodes.feature

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.Feature
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
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
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.SKIPPED

class RunnerFeature(
    parent: TreeNode,
    indexInParent: Int,
    featurePathFromRoot: List<String>,
    val featureName: String,
    val tags: List<String>,
    val feature: Feature
) : RunnerFeatureOrTest(), ContainerTreeNode {

    override val parent: RunnerTreeNode = parent as? RunnerTreeNode
        ?: throw IllegalArgumentException("unexpected parent note type [${parent.javaClass}]: [$parent]")

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

    override val positionInParent = PositionInParent(
        id = featurePathFromRoot.joinToString(separator = "/") + "/${Feature.FILE_NAME_WITH_EXTENSION}",
        indexInParent = indexInParent
    )

    override fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute feature [$featureName]", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logFeatureStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            val dynamicVars = DynamicVariablesContext()
            val vars = VariablesContext.forTest(dynamicVars, globalVars)
            context.testVariables.setVariablesContext(vars)

            // before all hooks
            status = beforeHooks.run(context, globalVars)

            // children
            for (featureOrTest in children) {
                if (status <= PASSED) {
                    val featureOrTestStatus: ExecutionStatus = featureOrTest.run(context, globalVars)

                    if (featureOrTestStatus > status) {
                        status = featureOrTestStatus
                    }
                } else {
                    featureOrTest.skip(context)
                }
            }

            // after all hooks
            val afterHooksStatus = afterHooks.run(context, globalVars)
            if (afterHooksStatus > status) {
                status = afterHooksStatus
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            logFeatureEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
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
