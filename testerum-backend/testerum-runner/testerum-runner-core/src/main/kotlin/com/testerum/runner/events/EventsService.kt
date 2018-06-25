package com.testerum.runner.events

import com.testerum.runner.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner.events.model.RunnerEvent

class EventsService(private val executionListenerFinder: ExecutionListenerFinder) {

    fun logEvent(runnerEvent: RunnerEvent) {
        executionListenerFinder.findExecutionListener().onEvent(runnerEvent)
    }

}