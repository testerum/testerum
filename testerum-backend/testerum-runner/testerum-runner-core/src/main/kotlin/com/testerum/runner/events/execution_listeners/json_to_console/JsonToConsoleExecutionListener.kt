package com.testerum.runner.events.execution_listeners.json_to_console

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import java.lang.Appendable

class JsonToConsoleExecutionListener(private var objectMapper: ObjectMapper) : ExecutionListener {

    companion object {
        // note: if you change these, also change it in net.qutester.service.tests_runner.event_processor.TestRunnerEventProcessor
        const val TESTERUM_EVENT_PREFIX = "-->testerum\u0000-->";
        const val TESTERUM_EVENT_SUFFIX = "<--testerum\u0000<--";
    }

    private val appendable: Appendable = System.out

    override fun onEvent(event: RunnerEvent) {
        appendable.append(TESTERUM_EVENT_PREFIX)
        appendable.append(objectMapper.writeValueAsString(event))
        appendable.append(TESTERUM_EVENT_SUFFIX)
        appendable.append("\n")
    }

}