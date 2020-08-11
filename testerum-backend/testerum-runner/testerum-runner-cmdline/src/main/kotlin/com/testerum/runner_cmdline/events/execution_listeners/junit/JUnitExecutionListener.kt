package com.testerum.runner_cmdline.events.execution_listeners.junit

import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.ParametrizedTestEndEvent
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import java.util.concurrent.LinkedBlockingDeque
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class JUnitExecutionListener : BaseExecutionListener() {

    val eventQueue: LinkedBlockingDeque<RunnerEvent> = LinkedBlockingDeque<RunnerEvent>()

    fun setJUnitNotifierAndDescriptions() {
    }

    override fun onSuiteStart(event: SuiteStartEvent) {
        eventQueue.put(event)
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        eventQueue.put(event)
    }

    override fun onFeatureStart(event: FeatureStartEvent) {
        eventQueue.put(event)
    }

    override fun onFeatureEnd(event: FeatureEndEvent) {
        eventQueue.put(event)
    }

    override fun onTestStart(event: TestStartEvent) {
        eventQueue.put(event)
    }

    override fun onTestEnd(event: TestEndEvent) {
        eventQueue.put(event)
    }

    override fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {
        eventQueue.put(event)
    }

    override fun onParametrizedTestEnd(event: ParametrizedTestEndEvent) {
        eventQueue.put(event)
    }

    override fun onScenarioStart(event: ScenarioStartEvent) {
        eventQueue.put(event)
    }

    override fun onScenarioEnd(event: ScenarioEndEvent) {
        eventQueue.put(event)
    }

    override fun onStepStart(event: StepStartEvent) {
        eventQueue.put(event)
    }

    override fun onStepEnd(event: StepEndEvent) {
        eventQueue.put(event)
    }

    override fun onTextLog(event: TextLogEvent) {
        eventQueue.put(event)
    }

    override fun onUnknownEvent(event: RunnerEvent) {
        eventQueue.put(event)
    }
}
