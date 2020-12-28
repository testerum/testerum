package com.testerum.runner_cmdline.runner_tree.nodes.test

import com.testerum.common_kotlin.indent
import com.testerum.model.test.TestModel
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.DynamicVarsContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVarsContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.DISABLED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.FAILED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.SKIPPED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.UNDEFINED
import java.nio.file.Path as JavaPath

class RunnerTest(
    parent: TreeNode,
    val test: TestModel,
    val filePath: JavaPath,
    val indexInParent: Int,
) : RunnerFeatureOrTest(), ContainerTreeNode {

    override val parent: RunnerTreeNode = parent as? RunnerTreeNode
        ?: throw IllegalArgumentException("unexpected parent note type [${parent.javaClass}]: [$parent]")

    override val positionInParent = PositionInParent(test.id, indexInParent)

    private val children = mutableListOf<RunnerStep>()

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        children += child as? RunnerStep
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

    override fun run(context: RunnerContext, globalVars: GlobalVarsContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute test at [${filePath.toAbsolutePath().normalize()}]", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVarsContext): ExecutionStatus {
        if (test.properties.isDisabled) {
            return disable(context)
        }

        logTestStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            context.glueObjectFactory.beforeTest()

            val dynamicVars = DynamicVarsContext()
            val vars = VariablesContext.forTest(dynamicVars, globalVars)
            context.testVariables.setVariablesContext(vars)

            // before all hooks
            status = beforeHooks.run(context, globalVars)

            if (children.isEmpty()) {
                status = UNDEFINED
                context.logMessage("marking test [${test.name}] at [${test.path}] as $status because it doesn't have any steps")
            } else {
                for (step in children) {
                    if (status <= PASSED) {
                        val stepStatus: ExecutionStatus = step.run(context, vars)

                        if (stepStatus > status) {
                            status = stepStatus
                        }
                    } else {
                        step.skip(context)
                    }
                }
            }

            // after all hooks
            val afterAllHooksStatus = afterHooks.run(context, globalVars)
            if (afterAllHooksStatus > status) {
                status = afterAllHooksStatus
            }
        } catch (e: Exception) {
            status = FAILED
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

            logTestEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
    }

    override fun skip(context: RunnerContext): ExecutionStatus {
        logTestStart(context)

        var executionStatus = SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (step in children) {
                step.skip(context)
            }
        } catch (e: Exception) {
            executionStatus = FAILED
            exception = e
        } finally {
            logTestEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    private fun disable(context: RunnerContext): ExecutionStatus {
        logTestStart(context)

        var executionStatus = DISABLED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (step in children) {
                step.disable(context)
            }
        } catch (e: Exception) {
            executionStatus = FAILED
            exception = e
        } finally {
            logTestEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    private fun logTestStart(context: RunnerContext) {
        context.logEvent(
            TestStartEvent(
                eventKey = eventKey,
                testName = test.name,
                testFilePath = test.path,
                tags = test.tags
            )
        )
        context.logMessage("Started executing test [${test.name}] at [${test.path}]")
    }

    private fun logTestEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage("Finished executing test [${test.name}] at [${test.path}]; status: [$executionStatus]", exception)
        context.logEvent(
            TestEndEvent(
                eventKey = eventKey,
                testFilePath = test.path,
                testName = test.name,
                status = executionStatus,
                durationMillis = durationMillis
            )
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        // test info
        destination.indent(indentLevel).append("test")
        if (test.properties.isDisabled) {
            destination.append(" DISABLED")
        }
        destination.append(" '").append(test.name).append("'")
        if (test.tags.isNotEmpty()) {
            destination.append(", tags=").append(test.tags)
        }
        destination.append("\n")

        beforeHooks.addToString(destination, indentLevel + 1)

        for (step in children) {
            step.addToString(destination, indentLevel + 1)
        }

        afterHooks.addToString(destination, indentLevel + 1)
    }
}
