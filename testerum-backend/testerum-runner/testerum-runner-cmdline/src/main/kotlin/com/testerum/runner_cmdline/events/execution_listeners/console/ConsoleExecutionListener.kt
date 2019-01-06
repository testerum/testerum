package com.testerum.runner_cmdline.events.execution_listeners.console

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_cmdline.banner.TesterumBanner
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.events.execution_listeners.utils.events_stack.ExecutionEventsStack
import java.time.format.DateTimeFormatter

class ConsoleExecutionListener : BaseExecutionListener() {

    companion object {
        private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    }

    // todo: allow the listeners to access the execution tree (to know from the beginning how many tests there are, etc.)
    // ===> this allows us to e.g. align "==> [STATUS]" for all tests, because we know how many characters is the longest test name

    // todo: share this stack among all listeners, to minimize memory
    private val eventsStack = ExecutionEventsStack()
    private val failedTests = ArrayList<TestEndEvent>()

    override fun start() {
        print(TesterumBanner.BANNER + "\n\n")
    }

    override fun onEvent(event: RunnerEvent) {
        eventsStack.push(event)
        super.onEvent(event)
    }

    override fun onTestStart(event: TestStartEvent) {
        print("* Test [${event.testName}] (at [${event.testFilePath}])")
    }

    override fun onTestEnd(event: TestEndEvent) {
        print(" ===> ${event.status} (in ${event.durationMillis} ms)\n")
        if (event.status == ExecutionStatus.FAILED) {
            failedTests += event

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
