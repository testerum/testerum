package com.testerum.runner_cmdline.events.execution_listeners.stats

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
import com.testerum.runner.statistics_model.events_aggregators.StatsAggregator
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer

class StatsExecutionListener : ExecutionListener {

    companion object {
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

    private val aggregator = StatsAggregator()

    override fun start() { }

    override fun onEvent(event: RunnerEvent) {
        aggregator.aggregate(event)
    }

    override fun stop() {
        val serializedStats = OBJECT_MAPPER.writeValueAsString(
                aggregator.getResult()
        )

        ConsoleOutputCapturer.getOriginalTextWriter().print(
                "stats: $serializedStats\n"
        )
    }
}
