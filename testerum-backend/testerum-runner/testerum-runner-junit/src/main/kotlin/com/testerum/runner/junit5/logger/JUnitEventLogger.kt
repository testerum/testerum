package com.testerum.runner.junit5.logger

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
import com.testerum.runner_cmdline.events.execution_listeners.console_debug.ConsoleDebugExecutionListener

class JUnitEventLogger: ConsoleDebugExecutionListener() {
    override fun onSuiteStart(event: SuiteStartEvent) {
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
    }

    override fun onFeatureStart(event: FeatureStartEvent) {
    }

    override fun onFeatureEnd(event: FeatureEndEvent) {
    }

    override fun onTestStart(event: TestStartEvent) {
        super.onTestStart(event)
    }

    override fun onTestEnd(event: TestEndEvent) {
        super.onTestEnd(event)
    }

    override fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {
    }

    override fun onParametrizedTestEnd(event: ParametrizedTestEndEvent) {
    }

    override fun onScenarioStart(event: ScenarioStartEvent) {
        super.onScenarioStart(event)
    }

    override fun onScenarioEnd(event: ScenarioEndEvent) {
        super.onScenarioEnd(event)
    }

    override fun onStepStart(event: StepStartEvent) {
        super.onStepStart(event)
    }

    override fun onStepEnd(event: StepEndEvent) {
        super.onStepEnd(event)
    }

    override fun onTextLog(event: TextLogEvent) {
        super.onTextLog(event)
    }

    override fun onUnknownEvent(event: RunnerEvent) {
        super.onUnknownEvent(event)
    }
}