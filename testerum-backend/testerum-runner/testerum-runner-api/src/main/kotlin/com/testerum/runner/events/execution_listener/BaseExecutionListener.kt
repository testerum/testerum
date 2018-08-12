package com.testerum.runner.events.execution_listener

import com.testerum.runner.events.model.*

open class BaseExecutionListener : ExecutionListener {

    /**
     * if you want to override this method, implement [ExecutionListener] instead
     */
    final override fun onEvent(event: RunnerEvent) {
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

}
