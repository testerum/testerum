package com.testerum.report_generators.reports.console_debug

import com.testerum.report_generators.reports.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.ConfigurationEvent
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.HooksEndEvent
import com.testerum.runner.events.model.HooksStartEvent
import com.testerum.runner.events.model.ParametrizedTestEndEvent
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent

open class ConsoleDebugExecutionListener : BaseExecutionListener() {

    private var indentLevel = 0

    override fun onConfigurationEvent(event: ConfigurationEvent) {
        log("CONFIGURATION [${event.eventKey}] : $event\n")
    }

    override fun onSuiteStart(event: SuiteStartEvent) {
        indent()
        log("SUITE_START [${event.eventKey}]\n")

        indentLevel++
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        indentLevel--
        indent()

        log("SUITE_END [${event.eventKey}] : status=${event.status}, durationMillis=${event.durationMillis}\n")
    }

    override fun onFeatureStart(event: FeatureStartEvent) {
        indent()
        log("FEATURE_START [${event.eventKey}] : featureName='${event.featureName}'\n")

        indentLevel++
    }

    override fun onFeatureEnd(event: FeatureEndEvent) {
        indentLevel--
        indent()

        log("FEATURE_END [${event.eventKey}] : status=${event.status}, featureName='${event.featureName}', durationMillis=${event.durationMillis}\n")
    }

    override fun onTestStart(event: TestStartEvent) {
        indent()
        log("TEST_START [${event.eventKey}] : testName='${event.testName}', testFilePath='${event.testFilePath}'\n")

        indentLevel++
    }

    override fun onTestEnd(event: TestEndEvent) {
        indentLevel--
        indent()

        log("TEST_END [${event.eventKey}] : status=${event.status}, testName='${event.testName}', testFilePath='${event.testFilePath}', durationMillis=${event.durationMillis}\n")
    }

    override fun onHooksStart(event: HooksStartEvent) {
        indent()
        log("TEST_START [${event.eventKey}] : hooks phase='${event.hookPhase}'\n")

        indentLevel++
    }

    override fun onHooksEnd(event: HooksEndEvent) {
        indentLevel--
        indent()

        log("TEST_END [${event.eventKey}] : status=${event.status}, hooks phase='${event.hookPhase}', durationMillis=${event.durationMillis}\n")
    }

    override fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {
        indent()
        log("PARAMETRIZED_TEST_START [${event.eventKey}] : testName='${event.testName}', testFilePath='${event.testFilePath}'\n")

        indentLevel++
    }

    override fun onParametrizedTestEnd(event: ParametrizedTestEndEvent) {
        indentLevel--
        indent()

        log("PARAMETRIZED_TEST_END [${event.eventKey}] : status=${event.status}, testName='${event.testName}', testFilePath='${event.testFilePath}', durationMillis=${event.durationMillis}\n")
    }

    override fun onScenarioStart(event: ScenarioStartEvent) {
        indent()
        log("SCENARIO_START [${event.eventKey}] : scenarioName='${event.scenario.name.orEmpty()}', scenarioIndex=${event.scenarioIndex}, testName='${event.testName}', testFilePath='${event.testFilePath}'\n")

        indentLevel++
    }

    override fun onScenarioEnd(event: ScenarioEndEvent) {
        indentLevel--
        indent()

        log("SCENARIO_END [${event.eventKey}] : status=${event.status}, scenarioName='${event.scenario.name.orEmpty()}', scenarioIndex=${event.scenarioIndex}, testName='${event.testName}', testFilePath='${event.testFilePath}', durationMillis=${event.durationMillis}\n")
    }

    override fun onStepStart(event: StepStartEvent) {
        indent()
        log("STEP_START [${event.eventKey}] : stepCall='${event.stepCall}'\n")

        indentLevel++
    }

    override fun onStepEnd(event: StepEndEvent) {
        indentLevel--
        indent()

        log("STEP_END [${event.eventKey}] : status=${event.status}, stepCall='${event.stepCall}', durationMillis=${event.durationMillis}\n")
    }

    override fun onTextLog(event: TextLogEvent) {
        indent()
        log(
                buildString {
                    append("TEXT_LOG [${event.eventKey}] : ")
                    append(event.logLevel.formatForLogging)
                    append(" ")
                    append(
                            event.message
                                    .lines()
                                    .joinToString(separator = "\n${"    ".repeat(indentLevel + 1)}")
                    )
                    append("\n")
                    val exceptionDetail = event.exceptionDetail
                    if (exceptionDetail != null) {
                        append(
                                exceptionDetail.detailedToString()
                                        .removeSuffix("\n")
                                        .lines()
                                        .joinToString(separator = "\n") {
                                            "    ".repeat(indentLevel + 1) + it
                                        }
                        )
                        append("\n")
                    }
                }
        )
    }

    override fun onUnknownEvent(event: RunnerEvent) {
        indent()
        log("UNKNOWN_EVENT [${event.eventKey}] : [${event.javaClass.name}]\n")
    }

    private fun indent() {
        log("    ".repeat(indentLevel))
    }

    private fun log(text: String) {
        ConsoleOutputCapturer.getOriginalTextWriter().print(text)
    }
}
