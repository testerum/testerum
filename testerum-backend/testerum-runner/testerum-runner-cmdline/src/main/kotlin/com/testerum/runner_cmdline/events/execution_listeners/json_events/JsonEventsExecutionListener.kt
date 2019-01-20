package com.testerum.runner_cmdline.events.execution_listeners.json_events

import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner_cmdline.events.execution_listeners.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.TextPrinter
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.impl.FileTextPrinter
import java.nio.file.Paths

class JsonEventsExecutionListener(private val properties: Map<String, String>) : ExecutionListener {

    companion object {
        // IMPORTANT: if you change these, also change it in com.testerum.web_backend.services.runner.execution.TestRunnerEventParser
        private const val TESTERUM_EVENT_PREFIX  = "-->testerum\u0000-->"
        private const val TESTERUM_EVENT_POSTFIX = "<--testerum\u0000<--"
    }

    private val textPrinter: TextPrinter = run {
        val destinationFileName = properties[EventListenerProperties.JsonEvents.DESTINATION_FILE_NAME]
        if (destinationFileName == null) {
            return@run ConsoleOutputCapturer.getOriginalTextWriter()
        } else {
            return@run FileTextPrinter(
                    Paths.get(destinationFileName)
            )
        }
    }

    private val wrapJsonWithPrefixAndPostfix: Boolean = run {
        val property = properties[EventListenerProperties.JsonEvents.WRAP_JSON_WITH_PREFIX_AND_POSTFIX]

        if (property == null) {
            false
        } else {
            property.toBoolean()
        }
    }

    override fun start() {}

    override fun onEvent(event: RunnerEvent) {
        val serializedEvent = EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(event)

        if (wrapJsonWithPrefixAndPostfix) {
            textPrinter.print("$TESTERUM_EVENT_PREFIX$serializedEvent$TESTERUM_EVENT_POSTFIX\n")
        } else {
            textPrinter.print("$serializedEvent\n")
        }
    }

    override fun stop() {
        textPrinter.close()
    }

}
