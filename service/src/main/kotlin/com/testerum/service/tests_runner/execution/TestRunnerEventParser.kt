package com.testerum.service.tests_runner.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.runner.events.model.*
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class TestRunnerEventParser(private val jsonObjectMapper: ObjectMapper,
                            private val eventProcessor: (event: RunnerEvent) -> Unit) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TestRunnerEventParser::class.java)

        // IMPORTANT: if you change these, also change it in com.testerum.runner.events.execution_listeners.json_to_console.JsonToConsoleExecutionListener
        private const val TESTERUM_EVENT_PREFIX = "-->testerum\u0000-->"
        private const val TESTERUM_EVENT_SUFFIX = "<--testerum\u0000<--"
    }

    private val eventKeysStack = mutableListOf<EventKey>()

    fun processEvent(eventAsString: String) {
        try {
            processEventWithoutErrorHandling(eventAsString)
        } catch(e: Exception) {
            LOGGER.error("Exception while processing runner event: [$eventAsString]", e)
        }
    }

    private fun processEventWithoutErrorHandling(eventAsString: String) {
//        LOGGER.debug("event line: [{}]", eventAsString)

        if (eventAsString.startsWith(TESTERUM_EVENT_PREFIX) && eventAsString.endsWith(TESTERUM_EVENT_SUFFIX)) {
            val json: String = eventAsString.removePrefix(TESTERUM_EVENT_PREFIX)
                                            .removeSuffix(TESTERUM_EVENT_SUFFIX)
            val testRunnerEvent = jsonObjectMapper.readValue<RunnerEvent>(json)

            processTestRunnerEvent(testRunnerEvent)
        } else {
            eventProcessor(
                    TextLogEvent(
                            time = LocalDateTime.now(),
                            eventKey = eventKeysStack.lastOrNull() ?: EventKey.SUITE_EVENT_KEY,
                            logLevel = LogLevel.DEBUG,
                            message = eventAsString
                    )
            )
        }
    }

    private fun processTestRunnerEvent(event: RunnerEvent) {
        when (event) {
            is SuiteStartEvent -> { eventKeysStack.clear(); eventKeysStack.add(event.eventKey); }
            is TestStartEvent  -> eventKeysStack.add(event.eventKey)
            is StepStartEvent  -> eventKeysStack.add(event.eventKey)
            is SuiteEndEvent   -> eventKeysStack.removeAt(eventKeysStack.lastIndex)
            is TestEndEvent    -> eventKeysStack.removeAt(eventKeysStack.lastIndex)
            is StepEndEvent    -> eventKeysStack.removeAt(eventKeysStack.lastIndex)
        }

        val eventToProcess: RunnerEvent = if (event is TextLogEvent) {
            event.copy(
                    eventKey = eventKeysStack.lastOrNull() ?: EventKey.SUITE_EVENT_KEY
            )
        } else {
            event
        }

        eventProcessor(eventToProcess)
    }

}