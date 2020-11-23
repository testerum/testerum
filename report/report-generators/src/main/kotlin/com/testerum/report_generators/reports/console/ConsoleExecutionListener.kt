package com.testerum.report_generators.reports.console

import com.testerum.common_kotlin.emptyToNull
import com.testerum.report_generators.reports.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.report_generators.reports.utils.events_stack.ExecutionEventsStack
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import java.time.format.DateTimeFormatter

class ConsoleExecutionListener : BaseExecutionListener() {

    companion object {
        private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    }

    // todo: allow the listeners to access the execution tree (to know from the beginning how many tests there are, etc.)
    // ===> this allows us to e.g. align "==> [STATUS]" for all tests, because we know how many characters is the longest test name

    // todo: share this stack among all listeners, to minimize memory
    private val eventsStack = ExecutionEventsStack()

    private var isBeforeFirstTest = true

    override fun start() {}

    override fun onEvent(event: RunnerEvent) {
        eventsStack.push(event)
        super.onEvent(event)
    }

    override fun onSuiteStart(event: SuiteStartEvent) {
        isBeforeFirstTest = false
    }

    override fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {
        print("* Parametrized Test [${event.testName}] (at [${event.testFilePath}])\n")
    }

    override fun onScenarioStart(event: ScenarioStartEvent) {
        print("*   Scenario [${event.scenario.name.emptyToNull() ?: "Scenario ${event.scenarioIndex + 1}"}]")
    }

    override fun onScenarioEnd(event: ScenarioEndEvent) {
        print(" ===> ${event.status} (in ${event.durationMillis} ms)\n")
        if (event.status == ExecutionStatus.FAILED) {
            val logs = eventsStack.peek(
                    untilItemType = ScenarioStartEvent::class.java,
                    desiredItemType = TextLogEvent::class.java
            )

            for (log in logs) {
                print(formatTextLogEvent(log, indentLevel = 1))
            }
        }
    }

    override fun onTestStart(event: TestStartEvent) {
        print("* Test [${event.testName}] (at [${event.testFilePath}])")
    }

    override fun onTestEnd(event: TestEndEvent) {
        print(" ===> ${event.status} (in ${event.durationMillis} ms)\n")
        if (event.status == ExecutionStatus.FAILED) {
            val logs = eventsStack.peek(
                    untilItemType = TestStartEvent::class.java,
                    desiredItemType = TextLogEvent::class.java
            )

            for (log in logs) {
                print(formatTextLogEvent(log, indentLevel = 1))
            }
        }
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        print("\nTest suite status: ${event.status}\n")
    }

    // log only the errors before initialization because all the other errors are logged at the end of the test execution
    override fun onTextLog(event: TextLogEvent) {
        if (!isBeforeFirstTest) {
            return
        }

        if (event.logLevel == LogLevel.ERROR) {
            print(formatTextLogEvent(event, indentLevel = 0))
        }
    }

    private fun formatTextLogEvent(logEvent: TextLogEvent,
                                   indentLevel: Int = 0): String {
        val timestamp = TIMESTAMP_FORMATTER.format(logEvent.time)
        val logLevel = logEvent.logLevel.formatForLogging
        val message = logEvent.message
        val exceptionWithStackTrace = logEvent.exceptionDetail?.asDetailedString

        return buildString {
            for (i in 1..indentLevel) {
                append("    ")
            }
            append(timestamp).append("  ").append(logLevel).append(" ").append(message)
            if (exceptionWithStackTrace != null) {
                append("; exception:\n")
                append(
                        exceptionWithStackTrace
                                .removeSuffix("\n")
                                .lines()
                                .joinToString(separator = "\n") {
                                    "    ".repeat(indentLevel + 1) + it
                                }
                )
            }
            append('\n')
        }
    }

    private fun print(text: String) = ConsoleOutputCapturer.getOriginalTextWriter().print(text)

}
