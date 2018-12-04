package com.testerum.runner_cmdline.events

import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder

class EventsService(private val executionListenerFinder: ExecutionListenerFinder) {

    fun logEvent(runnerEvent: RunnerEvent) {
        for (executionListener in executionListenerFinder.executionListeners) {
            executionListener.onEvent(runnerEvent)
        }
    }

}