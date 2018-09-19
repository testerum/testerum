package com.testerum.runner_cmdline.events.execution_listeners.json_to_console

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent

class JsonToConsoleExecutionListener : ExecutionListener {

    companion object {
        // IMPORTANT: if you change these, also change it in com.testerum.service.tests_runner.execution.TestRunnerEventParser
        const val TESTERUM_EVENT_PREFIX = "-->testerum\u0000-->"
        const val TESTERUM_EVENT_SUFFIX = "<--testerum\u0000<--"

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

    override fun onEvent(event: RunnerEvent) {
        println("$TESTERUM_EVENT_PREFIX${OBJECT_MAPPER.writeValueAsString(event)}$TESTERUM_EVENT_SUFFIX")
    }

}
