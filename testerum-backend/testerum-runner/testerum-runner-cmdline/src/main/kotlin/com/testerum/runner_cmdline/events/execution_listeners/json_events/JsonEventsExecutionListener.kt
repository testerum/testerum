package com.testerum.runner_cmdline.events.execution_listeners.json_events

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.runner.cmdline.EventListenerProperties
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.TextPrinter
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.impl.FileTextPrinter
import java.nio.file.Paths

class JsonEventsExecutionListener constructor(private val properties: Map<String, String>) : ExecutionListener {

    companion object {
        val VALID_PROPERTY_KEYS = listOf(EventListenerProperties.JsonEvents.DESTINATION_FILE_NAME)
        val VALID_PROPERTY_KEYS_TEXT = VALID_PROPERTY_KEYS.joinToString(separator = ", ", prefix = "", postfix = "") { "[$it]" }

        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    init {
        // validate properties
        val invalidPropertyKeys = properties.keys - VALID_PROPERTY_KEYS

        if (invalidPropertyKeys.isNotEmpty()) {
            throw IllegalArgumentException("Invalid JSON_EVENTS output format properties: $invalidPropertyKeys; valid properties are: $VALID_PROPERTY_KEYS_TEXT")
        }
    }

    override fun start() { }

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

    override fun onEvent(event: RunnerEvent) {
        textPrinter.print("${OBJECT_MAPPER.writeValueAsString(event)}\n")
    }

    override fun stop() {
        textPrinter.close()
    }

}
