package com.testerum.runner_cmdline.events.execution_listeners.console_debug

import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class ConsoleDebugExecutionListener : BaseExecutionListener() {

    private var indentLevel = 0

    override fun onSuiteStart(event: SuiteStartEvent) {
        indent()
        log("SUITE_START: eventKey=${event.eventKey}\n")

        indentLevel++
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        indentLevel--
        indent()

        log("SUITE_END: status=${event.status}, durationMillis=${event.durationMillis}, eventKey=${event.eventKey}\n")
    }

    override fun onFeatureStart(event: FeatureStartEvent) {
        indent()
        log("FEATURE_START: featureName='${event.featureName}', eventKey=${event.eventKey}\n")

        indentLevel++
    }

    override fun onFeatureEnd(event: FeatureEndEvent) {
        indentLevel--
        indent()

        log(
                buildString {
                    append("FEATURE_END: ")
                    append("status=${event.status}")
                    if (event.exceptionDetail != null) {
                        append(", exceptionDetail='${event.exceptionDetail}'")
                    }
                    append(", featureName='${event.featureName}'")
                    append(", durationMillis=${event.durationMillis}")
                    append(", eventKey=${event.eventKey}")
                    append('\n')
                }
        )
    }

    override fun onTestStart(event: TestStartEvent) {
        indent()
        log("TEST_START: testName='${event.testName}', testFilePath='${event.testFilePath}', eventKey=${event.eventKey}\n")

        indentLevel++
    }

    override fun onTestEnd(event: TestEndEvent) {
        indentLevel--
        indent()

        log(
                buildString {
                    append("TEST_END: ")
                    append("status=${event.status}")
                    if (event.exceptionDetail != null) {
                        append(", exceptionDetail='${event.exceptionDetail}'")
                    }
                    append(", testName='${event.testName}'")
                    append(", testFilePath='${event.testFilePath}'")
                    append(", durationMillis=${event.durationMillis}")
                    append(", eventKey=${event.eventKey}")
                    append('\n')
                }
        )
    }

    override fun onStepStart(event: StepStartEvent) {
        indent()
        log("STEP_START: stepCall='${event.stepCall}', eventKey=${event.eventKey}\n")

        indentLevel++
    }

    override fun onStepEnd(event: StepEndEvent) {
        indentLevel--
        indent()

        val exceptionDetail = event.exceptionDetail

        log(
                buildString {
                    append("STEP_END: ")
                    append("status=${event.status}")
                    if (exceptionDetail != null) {
                        append(", exceptionDetail='$exceptionDetail'")
                    }
                    append(", stepCall='${event.stepCall}'")
                    append(", durationMillis=${event.durationMillis}")
                    append(", eventKey=${event.eventKey}")
                    append('\n')
                }
        )
        if (exceptionDetail != null) {
            log(
                    exceptionDetail.detailedToString()
                            .lines()
                            .filter { it.isNotBlank() }
                            .joinToString(separator = "\n") {
                                "    ".repeat(indentLevel + 1) + it
                            }
                            + "\n"
            )
        }
    }

    override fun onTextLog(event: TextLogEvent) {
        indent()
        log("TEXT_LOG: message='${event.message}', eventKey=${event.eventKey}\n")
    }

    override fun onUnknownEvent(event: RunnerEvent) {
        indent()
        log("UNKNOWN_EVENT [${event.javaClass.name}]\n")
    }

    private fun indent() {
        log("    ".repeat(indentLevel))
    }

    private fun log(text: String) {
        ConsoleOutputCapturer.getOriginalTextWriter().print(text)
    }
}
