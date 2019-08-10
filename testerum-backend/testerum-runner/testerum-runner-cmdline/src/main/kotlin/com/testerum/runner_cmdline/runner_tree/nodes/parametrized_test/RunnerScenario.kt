package com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.file_service.mapper.business_to_file.BusinessToFileScenarioMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileScenarioParamMapper
import com.testerum.model.expressions.json.JsJson
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.test.scenario.param.ScenarioParamType
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTestException
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.DynamicVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParamSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RunnerScenario(private val beforeEachTestHooks: List<RunnerHook>,
                     private val test: TestModel,
                     private val scenario: Scenario,
                     private val scenarioIndex: Int,
                     private val filePath: java.nio.file.Path,
                     private val indexInParent: Int,
                     private val steps: List<RunnerStep>,
                     private val afterEachTestHooks: List<RunnerHook>) : RunnerFeatureOrTest() {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(RunnerHook::class.java)

        private val businessToFileScenarioMapper = BusinessToFileScenarioMapper(
                BusinessToFileScenarioParamMapper()
        )
    }

    init {
        for (step in steps) {
            step.parent = this
        }
    }

    override lateinit var parent: RunnerTreeNode
    override val positionInParent = PositionInParent("${test.id}-$scenarioIndex", indexInParent)

    private val scenarioName = scenario.name ?: "Execution ${scenarioIndex + 1}"

    private fun getPathForLogging(): String {
        return "scenario [$scenarioName] (index $scenarioIndex) of test at [${filePath.toAbsolutePath().normalize()}]"
    }

    private fun getNameForLogging(): String {
        return "scenario [$scenarioName] (index $scenarioIndex) of test [${test.name}] at [${test.path}]"
    }

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> {
        val glueClasses = mutableListOf<Class<*>>()

        for (hook in beforeEachTestHooks) {
            glueClasses += hook.getGlueClass(context)
        }
        for (step in steps) {
            glueClasses.addAll(
                    step.getGlueClasses(context)
            )
        }
        for (hook in afterEachTestHooks) {
            glueClasses += hook.getGlueClass(context)
        }

        return glueClasses
    }

    override fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute ${getPathForLogging()}", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        if (test.properties.isDisabled) {
            return disable(context)
        }

        logScenarioStart(context)

        var executionStatus: ExecutionStatus = ExecutionStatus.PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            val dynamicVars = DynamicVariablesContext()

            for (param in scenario.params) {
                val actualValue: Any? = when (param.type) {
                    ScenarioParamType.TEXT -> param.value
                    ScenarioParamType.JSON -> JsJson(param.value)
                }

                dynamicVars[param.name] = actualValue
            }

            context.glueObjectFactory.beforeTest()

            val vars = VariablesContext.forTest(dynamicVars, globalVars)
            context.testVariables.setVariablesContext(vars)

            try {
                for (hook in beforeEachTestHooks) {
                    if (executionStatus == ExecutionStatus.PASSED) {
                        val stepExecutionStatus: ExecutionStatus = hook.run(context)

                        executionStatus = stepExecutionStatus
                    } else {
                        hook.skip()
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "failed to execute ${HookPhase.BEFORE_EACH_TEST} hooks"

                LOG.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            if (steps.isEmpty()) {
                executionStatus = ExecutionStatus.UNDEFINED
                context.logMessage("marking ${getNameForLogging()} as $executionStatus because it doesn't have any steps")
            } else {
                for (step in steps) {
                    if (executionStatus == ExecutionStatus.PASSED) {
                        val stepExecutionStatus: ExecutionStatus = step.run(context, vars)

                        executionStatus = stepExecutionStatus
                    } else {
                        step.skip(context)
                    }
                }
            }

            var endHookStatus: ExecutionStatus = ExecutionStatus.PASSED
            try {
                for (hook in afterEachTestHooks) {
                    if (endHookStatus == ExecutionStatus.PASSED) {
                        val stepExecutionStatus: ExecutionStatus = hook.run(context)

                        endHookStatus = stepExecutionStatus
                    } else {
                        hook.skip()
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "failed to execute ${HookPhase.AFTER_EACH_TEST} hooks"

                LOG.error(errorMessage, e)

                throw RuntimeException(errorMessage, e)
            }

            if (executionStatus == ExecutionStatus.PASSED && endHookStatus != ExecutionStatus.PASSED) {
                executionStatus = endHookStatus
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
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

            logScenarioEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    override fun skip(context: RunnerContext): ExecutionStatus {
        logScenarioStart(context)

        var executionStatus = ExecutionStatus.SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (step in steps) {
                step.skip(context)
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logScenarioEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
            return executionStatus
        }
    }

    private fun disable(context: RunnerContext): ExecutionStatus {
        logScenarioStart(context)

        var executionStatus = ExecutionStatus.DISABLED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (step in steps) {
                step.disable(context)
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logScenarioEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
            return executionStatus
        }
    }

    private fun logScenarioStart(context: RunnerContext) {
        context.logEvent(
                ScenarioStartEvent(
                        eventKey = eventKey,
                        testName = test.name,
                        testFilePath = test.path,
                        scenario = scenario,
                        scenarioIndex = scenarioIndex,
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

    private fun logScenarioEnd(context: RunnerContext,
                               executionStatus: ExecutionStatus,
                               exception: Throwable?,
                               durationMillis: Long) {
        context.logMessage("Finished executing ${getNameForLogging()}; status: [$executionStatus]", exception)
        context.logEvent(
                ScenarioEndEvent(
                        eventKey = eventKey,
                        testFilePath = test.path,
                        testName = test.name,
                        scenario = scenario,
                        scenarioIndex = scenarioIndex,
                        status = executionStatus,
                        durationMillis = durationMillis
                )
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        // show test info
        destination.indent(indentLevel).append("scenario")
        if (test.properties.isDisabled) {
            destination.append(" DISABLED")
        }
        destination.append(" '").append(scenarioName).append("' (index $scenarioIndex) of test '").append(test.name).append("' at [").append(test.path).append("]")
        if (test.tags.isNotEmpty()) {
            destination.append(", tags=").append(test.tags)
        }
        destination.append("\n")

        // show children
        for (beforeEachTestHook in beforeEachTestHooks) {
            beforeEachTestHook.addToString(destination, indentLevel + 1)
        }

        for (step in steps) {
            step.addToString(destination, indentLevel + 1)
        }

        for (afterEachTestHook in afterEachTestHooks) {
            afterEachTestHook.addToString(destination, indentLevel + 1)
        }
    }
}
