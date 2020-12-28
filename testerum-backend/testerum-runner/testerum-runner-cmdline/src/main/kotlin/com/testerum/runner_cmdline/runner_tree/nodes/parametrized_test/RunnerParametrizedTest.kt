package com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test

import com.testerum.common_kotlin.indent
import com.testerum.model.test.TestModel
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.ParametrizedTestEndEvent
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.FAILED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.SKIPPED
import java.nio.file.Path as JavaPath

class RunnerParametrizedTest(
    parent: TreeNode,
    val test: TestModel,
    val filePath: JavaPath,
    val indexInParent: Int,
) : RunnerFeatureOrTest(), TreeNode {

    override val parent: RunnerTreeNode = parent as? RunnerTreeNode
        ?: throw IllegalArgumentException("unexpected parent note type [${parent.javaClass}]: [$parent]")

    override val positionInParent = PositionInParent(test.id, indexInParent)

    lateinit var scenarios: List<RunnerScenario>

    override fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute parametrized test at [${filePath.toAbsolutePath().normalize()}]", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logParametrizedTestStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (scenario in scenarios) {
                val scenarioStatus: ExecutionStatus = scenario.run(context, globalVars)

                if (scenarioStatus > status) {
                    status = scenarioStatus
                }
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            logParametrizedTestEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
    }

    override fun skip(context: RunnerContext): ExecutionStatus {
        logParametrizedTestStart(context)

        var status = SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (scenario in scenarios) {
                val featureOrTestStatus: ExecutionStatus = scenario.skip(context)

                if (featureOrTestStatus > status) {
                    status = featureOrTestStatus
                }
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            logParametrizedTestEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
    }

    private fun logParametrizedTestStart(context: RunnerContext) {
        context.logEvent(
            ParametrizedTestStartEvent(
                eventKey = eventKey,
                testName = test.name,
                testFilePath = test.path,
                tags = test.tags
            )
        )
        context.logMessage("Started executing parametrized test [${test.name}] at [${test.path}]")
    }

    private fun logParametrizedTestEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage("Finished executing parametrized test [${test.name}] at [${test.path}]; status: [$executionStatus]", exception)
        context.logEvent(
            ParametrizedTestEndEvent(
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
        // show test info
        destination.indent(indentLevel).append("parametrized-test")
        if (test.properties.isDisabled) {
            destination.append(" DISABLED")
        }
        destination.append(" '").append(test.name).append("'")
        if (test.tags.isNotEmpty()) {
            destination.append(", tags=").append(test.tags)
        }
        destination.append("\n")

        // show children
        for (scenario in scenarios) {
            scenario.addToString(destination, indentLevel + 1)
        }
    }

}
