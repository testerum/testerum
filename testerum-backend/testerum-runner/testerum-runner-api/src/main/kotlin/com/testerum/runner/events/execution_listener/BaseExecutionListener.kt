package com.testerum.runner.events.execution_listener

import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent

open class BaseExecutionListener : ExecutionListener {

    override fun start() { }

    override fun onEvent(event: RunnerEvent) {
        when (event) {
            is SuiteStartEvent   -> onSuiteStart(event)
            is SuiteEndEvent     -> onSuiteEnd(event)

            is FeatureStartEvent -> onFeatureStart(event)
            is FeatureEndEvent   -> onFeatureEnd(event)

            is TestStartEvent    -> onTestStart(event)
            is TestEndEvent      -> onTestEnd(event)

            is StepStartEvent    -> onStepStart(event)
            is StepEndEvent      -> onStepEnd(event)

            is TextLogEvent      -> onTextLog(event)

            else                 -> onUnknownEvent(event)
        }

    }

    protected open fun onSuiteStart(event: SuiteStartEvent) {}
    protected open fun onSuiteEnd(event: SuiteEndEvent) {}

    protected open fun onTestStart(event: TestStartEvent) {}
    protected open fun onTestEnd(event: TestEndEvent) {}

    protected open fun onFeatureStart(event: FeatureStartEvent) {}
    protected open fun onFeatureEnd(event: FeatureEndEvent) {}

    protected open fun onStepStart(event: StepStartEvent) {}
    protected open fun onStepEnd(event: StepEndEvent) {}

    protected open fun onTextLog(event: TextLogEvent) {}

    protected open fun onUnknownEvent(event: RunnerEvent) {
        throw IllegalArgumentException("unknown runner event [${event.javaClass.name}]")
    }

    override fun stop() {}

}
