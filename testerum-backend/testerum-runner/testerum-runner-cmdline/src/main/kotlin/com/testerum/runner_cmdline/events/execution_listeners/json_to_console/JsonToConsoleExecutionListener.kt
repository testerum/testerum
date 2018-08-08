package com.testerum.runner_cmdline.events.execution_listeners.json_to_console

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent

class JsonToConsoleExecutionListener(private var objectMapper: ObjectMapper) : ExecutionListener {

    companion object {
        // IMPORTANT: if you change these, also change it in com.testerum.service.tests_runner.execution.TestRunnerEventParser
        const val TESTERUM_EVENT_PREFIX = "-->testerum\u0000-->"
        const val TESTERUM_EVENT_SUFFIX = "<--testerum\u0000<--"
    }

    override fun onEvent(event: RunnerEvent) {
        println("$TESTERUM_EVENT_PREFIX${objectMapper.writeValueAsString(event)}$TESTERUM_EVENT_SUFFIX")
    }

}