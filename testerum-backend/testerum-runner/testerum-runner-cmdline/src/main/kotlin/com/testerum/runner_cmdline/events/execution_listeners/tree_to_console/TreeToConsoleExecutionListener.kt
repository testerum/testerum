package com.testerum.runner_cmdline.events.execution_listeners.tree_to_console

import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.*
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class TreeToConsoleExecutionListener : BaseExecutionListener() {

    private var indentLevel = 0

    override fun onSuiteStart(event: SuiteStartEvent) {
        indent()
        println("SUITE_START: eventKey=${event.eventKey}")

        indentLevel++
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        indentLevel--
        indent()

        println("SUITE_END: status=${event.status}, statistics=${event.statistics}, durationMillis=${event.durationMillis}, eventKey=${event.eventKey}")
    }

    override fun onTestStart(event: TestStartEvent) {
        indent()
        println("TEST_START: testName='${event.testName}', testFilePath='${event.testFilePath}', eventKey=${event.eventKey}")

        indentLevel++
    }

    override fun onTestEnd(event: TestEndEvent) {
        indentLevel--
        indent()

        println(
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
                }
        )
    }

    override fun onStepStart(event: StepStartEvent) {
        indent()
        println("STEP_START: stepCall='${event.stepCall}', eventKey=${event.eventKey}")

        indentLevel++
    }

    override fun onStepEnd(event: StepEndEvent) {
        indentLevel--
        indent()

        val exceptionDetail = event.exceptionDetail

        println(
                buildString {
                    append("STEP_END: ")
                    append("status=${event.status}")
                    if (exceptionDetail != null) {
                        append(", exceptionDetail='$exceptionDetail'")
                    }
                    append(", stepCall='${event.stepCall}'")
                    append(", durationMillis=${event.durationMillis}")
                    append(", eventKey=${event.eventKey}")
                }
        )
        if (exceptionDetail != null) {
            println(
                    exceptionDetail.detailedToString()
                            .lines()
                            .filter { it.isNotBlank() }
                            .joinToString(separator = "\n") {
                                "    ".repeat(indentLevel + 1) + it
                            }
            )
        }
    }

    override fun onTextLog(event: TextLogEvent) {
        indent()
        println("TEXT_LOG: message='${event.message}', eventKey=${event.eventKey}")
    }

    override fun onUnknownEvent(event: RunnerEvent) {
        indent()
        println("UNKNOWN_EVENT [${event.javaClass.name}]")
    }

    private fun indent() {
        print("    ".repeat(indentLevel))
    }
}