package net.qutester.service.tests_runner.event_bus

import com.testerum.runner.events.model.RunnerEvent

class TestRunnerEventBus {

    private val eventListeners: MutableList<TestRunnerEventListener> = mutableListOf()

    fun addEventListener(eventListener: TestRunnerEventListener) {
        eventListeners.add(eventListener)
    }

    fun removeEventListener(eventListener: TestRunnerEventListener) {
        eventListeners.remove(eventListener)
    }

    fun triggerEvent(event: RunnerEvent) {
        eventListeners.forEach { it.testSuiteEvent(event) }
    }
}