package net.qutester.service.tests_runner.event_processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.runner.events.model.*
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import net.qutester.service.tests_runner.event_bus.TestRunnerEventBus
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

// todo: make prototype (remove from spring)
class TestRunnerEventProcessor(private val jsonObjectMapper: ObjectMapper,
                               private val testRunnerEventBus: TestRunnerEventBus) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TestRunnerEventProcessor::class.java)

        // note: if you change these, also change it in com.testerum.runner.events.execution_listeners.json_to_console.JsonToConsoleExecutionListener
        private const val TESTERUM_EVENT_PREFIX = "-->testerum\u0000-->";
        private const val TESTERUM_EVENT_SUFFIX = "<--testerum\u0000<--";
    }

    private val eventKeysStack = mutableListOf<EventKey>()

    fun processEvent(eventAsString: String?) {
        try {
            processEventWithoutErrorHandling(eventAsString)
        } catch(e: Exception) {
            LOGGER.error("Exception while processing runner event: [${eventAsString}]", e)
        }
    }

    private fun processEventWithoutErrorHandling(eventAsString: String?) {
        if (eventAsString == null) {
            return
        }

        LOGGER.debug("event line: [$eventAsString]")

        if (eventAsString.startsWith(TESTERUM_EVENT_PREFIX) && eventAsString.endsWith(TESTERUM_EVENT_SUFFIX)) {
            val json = eventAsString
                    .removePrefix(TESTERUM_EVENT_PREFIX)
                    .removeSuffix(TESTERUM_EVENT_SUFFIX)
            var testRunnerEvent = jsonObjectMapper.readValue<RunnerEvent>(json)

            when (testRunnerEvent) {
                is SuiteStartEvent -> {
                    eventKeysStack.clear(); eventKeysStack.add(testRunnerEvent.eventKey); }
                is TestStartEvent -> eventKeysStack.add(testRunnerEvent.eventKey)
                is StepStartEvent -> eventKeysStack.add(testRunnerEvent.eventKey)
                is SuiteEndEvent -> eventKeysStack.removeAt(eventKeysStack.lastIndex)
                is TestEndEvent -> eventKeysStack.removeAt(eventKeysStack.lastIndex)
                is StepEndEvent -> eventKeysStack.removeAt(eventKeysStack.lastIndex)
            }

            if (testRunnerEvent is TextLogEvent) {
                testRunnerEvent = testRunnerEvent.copy(eventKey = eventKeysStack.lastOrNull()
                        ?: EventKey.SUITE_EVENT_KEY)
            }

            testRunnerEventBus.triggerEvent(testRunnerEvent)
        } else {
            // TODO: how should we handle runner exception on startup
            testRunnerEventBus.triggerEvent(
                    TextLogEvent(
                            time = LocalDateTime.now(),
                            eventKey = eventKeysStack.lastOrNull()?: EventKey.SUITE_EVENT_KEY,
                            logLevel = LogLevel.DEBUG,
                            message = eventAsString
                    )
            )
        }
    }
}