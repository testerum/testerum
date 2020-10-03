package com.testerum.runner.events.execution_listener

import com.testerum.runner.events.model.ConfigurationEvent
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

open class BaseExecutionListener : ExecutionListener {

    override fun start() { }

    override fun onEvent(event: RunnerEvent) {
        when (event) {
            is ConfigurationEvent         -> onConfigurationEvent(event)
            is SuiteStartEvent            -> onSuiteStart(event)
            is SuiteEndEvent              -> onSuiteEnd(event)

            is FeatureStartEvent          -> onFeatureStart(event)
            is FeatureEndEvent            -> onFeatureEnd(event)

            is TestStartEvent             -> onTestStart(event)
            is TestEndEvent               -> onTestEnd(event)

            is ParametrizedTestStartEvent -> onParametrizedTestStart(event)
            is ParametrizedTestEndEvent   -> onParametrizedTestEnd(event)

            is ScenarioStartEvent         -> onScenarioStart(event)
            is ScenarioEndEvent           -> onScenarioEnd(event)

            is StepStartEvent             -> onStepStart(event)
            is StepEndEvent               -> onStepEnd(event)

            is TextLogEvent               -> onTextLog(event)

            else                          -> onUnknownEvent(event)
        }

    }

    protected open fun onConfigurationEvent(event: ConfigurationEvent) {}

    protected open fun onSuiteStart(event: SuiteStartEvent) {}
    protected open fun onSuiteEnd(event: SuiteEndEvent) {}

    protected open fun onFeatureStart(event: FeatureStartEvent) {}
    protected open fun onFeatureEnd(event: FeatureEndEvent) {}

    protected open fun onTestStart(event: TestStartEvent) {}
    protected open fun onTestEnd(event: TestEndEvent) {}

    protected open fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {}
    protected open fun onParametrizedTestEnd(event: ParametrizedTestEndEvent) {}

    protected open fun onScenarioStart(event: ScenarioStartEvent) {}
    protected open fun onScenarioEnd(event: ScenarioEndEvent) {}

    protected open fun onStepStart(event: StepStartEvent) {}
    protected open fun onStepEnd(event: StepEndEvent) {}

    protected open fun onTextLog(event: TextLogEvent) {}

    protected open fun onUnknownEvent(event: RunnerEvent) {
        throw IllegalArgumentException("unknown runner event [${event.javaClass.name}]")
    }

    override fun stop() {}

}
