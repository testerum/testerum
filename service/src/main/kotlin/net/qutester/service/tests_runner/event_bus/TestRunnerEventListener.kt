package net.qutester.service.tests_runner.event_bus

import com.testerum.runner.events.model.RunnerEvent

interface TestRunnerEventListener {
    fun testSuiteEvent(runnerEvent: RunnerEvent)
}