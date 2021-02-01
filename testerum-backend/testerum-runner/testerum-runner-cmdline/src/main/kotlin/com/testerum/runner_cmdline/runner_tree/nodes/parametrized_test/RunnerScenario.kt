package com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test

import com.testerum.common_kotlin.indent
import com.testerum.file_service.mapper.business_to_file.BusinessToFileScenarioMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileScenarioParamMapper
import com.testerum.model.expressions.json.JsJson
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.test.scenario.param.ScenarioParam
import com.testerum.model.test.scenario.param.ScenarioParamType
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTestException
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParamSerializer
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.DISABLED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.FAILED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.SKIPPED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.UNDEFINED
import org.apache.commons.text.StringEscapeUtils
import java.nio.file.Path as JavaPath

class RunnerScenario(
    parent: TreeNode,
    private val test: TestModel,
    val scenario: Scenario,
    private val originalScenarioIndex: Int,
    private val filteredScenarioIndex: Int,
    private val filePath: JavaPath,
) : RunnerTreeNode(), TreeNode {

    companion object {
        private val businessToFileScenarioMapper = BusinessToFileScenarioMapper(
            BusinessToFileScenarioParamMapper()
        )
    }

    override val parent: RunnerTreeNode = parent as? RunnerTreeNode
        ?: throw IllegalArgumentException("unexpected parent note type [${parent.javaClass}]: [$parent]")

    private lateinit var children: List<RunnerStep>

    fun setChildren(children: List<RunnerStep>) {
        this.children = children
    }

    override val positionInParent = PositionInParent("${test.id}-$filteredScenarioIndex", filteredScenarioIndex)

    val scenarioName = scenario.name ?: "Scenario ${originalScenarioIndex + 1}"

    private lateinit var beforeHooks: RunnerBeforeHooksList
    private lateinit var afterHooks: RunnerAfterHooksList

    fun setBeforeHooks(beforeHooksList: RunnerBeforeHooksList) {
        this.beforeHooks = beforeHooksList
    }

    fun setAfterHooks(afterHooksList: RunnerAfterHooksList) {
        this.afterHooks = afterHooksList
    }

    fun execute(context: RunnerContext): ExecutionStatus {
        try {
            return tryToRun(context)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute ${getPathForLogging()}", e)
        }
    }

    private fun tryToRun(context: RunnerContext): ExecutionStatus {
        if (test.properties.isDisabled) {
            return disable(context)
        }

        if (!scenario.enabled) {
            return disable(context)
        }

        logScenarioStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        context.variablesContext.startScenario()
        try {
            context.glueObjectFactory.beforeTest()
            setArgs(context)

            // before all hooks
            val beforeHooksStatus = beforeHooks.execute(context)
            status = beforeHooksStatus

            // scenario
            if (children.isEmpty()) {
                status = UNDEFINED
                context.logMessage("marking ${getNameForLogging()} as $status because it doesn't have any steps")
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

            // after all hooks
            val afterAllHooksStatus = afterHooks.execute(context)
            if (afterAllHooksStatus > status) {
                status = afterAllHooksStatus
            }
        } catch (e: Exception) {
            status = FAILED
            exception = e
        } finally {
            context.variablesContext.endScenario()

            try {
                context.glueObjectFactory.afterTest()
            } catch (e: Exception) {
                val afterTestException = RuntimeException("glueObjectFactory.afterTest() failed", e)

                exception = RunnerTestException(
                    message = "scenario execution failure",
                    cause = exception,
                    suppressedException = afterTestException
                )
            }

            logScenarioEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
    }

    private fun setArgs(context: RunnerContext) {
        for (param in scenario.params) {
            setArg(context, param)
        }
    }

    private fun setArg(
        context: RunnerContext,
        param: ScenarioParam
    ) {
        val actualValue: Any = when (param.type) {
            ScenarioParamType.TEXT -> context.variablesContext.resolveIn(param.value)
            ScenarioParamType.JSON -> JsJson(
                context.variablesContext.resolveIn(param.value) { StringEscapeUtils.escapeJson(it) }
            )
        }

        context.variablesContext.set(
            name = param.name,
            value = actualValue
        )
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

    fun skip(context: RunnerContext): ExecutionStatus {
        logScenarioStart(context)

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
            logScenarioEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    private fun disable(context: RunnerContext): ExecutionStatus {
        logScenarioStart(context)

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
            logScenarioEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    private fun logScenarioStart(context: RunnerContext) {
        context.logEvent(
            ScenarioStartEvent(
                eventKey = eventKey,
                testName = test.name,
                testFilePath = test.path,
                scenario = scenario,
                scenarioIndex = filteredScenarioIndex,
                tags = test.tags
            )
        )
        context.logMessage("Started executing ${getNameForLogging()}")

        context.logMessage("")
        context.logMessage("Scenario params")
        context.logMessage("---------------")

        val fileScenario = businessToFileScenarioMapper.mapScenario(scenario)

        for (param in fileScenario.params) {
            context.logMessage(
                FileScenarioParamSerializer.serializeToString(param)
            )
        }
        context.logMessage("")
    }

    private fun logScenarioEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage("Finished executing ${getNameForLogging()}; status: [$executionStatus]", exception)
        context.logEvent(
            ScenarioEndEvent(
                eventKey = eventKey,
                testFilePath = test.path,
                testName = test.name,
                scenario = scenario,
                scenarioIndex = filteredScenarioIndex,
                status = executionStatus,
                durationMillis = durationMillis
            )
        )
    }

    private fun getPathForLogging(): String {
        return "scenario [$scenarioName] (index $originalScenarioIndex) of test at [${filePath.toAbsolutePath().normalize()}]"
    }

    private fun getNameForLogging(): String {
        return "scenario [$scenarioName] (index $originalScenarioIndex) of test [${test.name}] at [${test.path}]"
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        // show test info
        destination.indent(indentLevel).append("scenario")
        if (test.properties.isDisabled) {
            destination.append(" DISABLED")
        }
        destination.append(" '").append(scenarioName).append("' (index $originalScenarioIndex) of test '").append(test.name).append("' at [").append(test.path)
            .append("]")
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
