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

    override fun execute(context: RunnerContext): ExecutionStatus {
        try {
            return tryToRun(context)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute test at [${filePath.toAbsolutePath().normalize()}]", e)
        }
    }

    private fun tryToRun(context: RunnerContext): ExecutionStatus {
        if (test.properties.isDisabled) {
            return disable(context)
        }

        logTestStart(context)

        var status: ExecutionStatus = PASSED
        context.testContext.apply {
            testName = test.name
            testPath = test.path.toString()

            testStatus = status
        }
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        context.variablesContext.startTest()
        try {
            context.glueObjectFactory.beforeTest()

            // before hooks
            val beforeHooksStatus = beforeHooks.execute(context)
            status = beforeHooksStatus

            if (children.isEmpty()) {
                status = UNDEFINED
                context.logMessage("marking test [${test.name}] at [${test.path}] as $status because it doesn't have any steps")
            } else {
                if (beforeHooksStatus == PASSED) {
                    val childrenStatus = runChildren(context, status)
                    if (childrenStatus > status) {
                        status = childrenStatus
                    }
                } else {
                    skipChildren(context)
                }
            }

            // after hooks
            val afterAllHooksStatus = afterHooks.execute(context, status)
            if (afterAllHooksStatus > status) {
                status = afterAllHooksStatus
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            context.variablesContext.endTest()

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

    private fun runChildren(
        context: RunnerContext,
        overallStatus: ExecutionStatus,
    ): ExecutionStatus {
        var status = overallStatus

        for (child in children) {
            if (status <= PASSED) {
                val childStatus: ExecutionStatus = child.execute(context)

                if (childStatus > status) {
                    status = childStatus
                }
            } else {
                child.skip(context)
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
