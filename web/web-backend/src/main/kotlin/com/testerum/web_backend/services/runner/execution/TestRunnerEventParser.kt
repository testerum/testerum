package com.testerum.web_backend.services.runner.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.position.EventKey
import org.slf4j.LoggerFactory
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class TestRunnerEventParser(private val jsonObjectMapper: ObjectMapper,
                            private val eventProcessor: (event: RunnerEvent) -> Unit) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TestRunnerEventParser::class.java)
    }

    private val eventKeysStack = mutableListOf<EventKey>()

    fun processEvent(eventAsString: String) {
        try {
            processEventWithoutErrorHandling(eventAsString)
        } catch(e: Exception) {
            LOG.error("Exception while processing runner event: [$eventAsString]", e)
        }
    }

    private fun processEventWithoutErrorHandling(eventAsString: String) {
        val testRunnerEvent = jsonObjectMapper.readValue<RunnerEvent>(eventAsString)

        processTestRunnerEvent(testRunnerEvent)
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
