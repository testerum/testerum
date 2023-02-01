package com.testerum.runner.junit5.logger

import com.testerum.report_generators.reports.console_debug.ConsoleDebugExecutionListener
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.ParametrizedTestEndEvent
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent

// We use this class to hide some logs from the JUnit runner because
// they would be attached to a wrong node in the JUnit execution tree
class JUnitEventLogger: ConsoleDebugExecutionListener() {

    override fun onSuiteStart(event: SuiteStartEvent) {}

    override fun onSuiteEnd(event: SuiteEndEvent) {}

    override fun onFeatureStart(event: FeatureStartEvent) {}

    override fun onFeatureEnd(event: FeatureEndEvent) {}

    override fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {}

    override fun onParametrizedTestEnd(event: ParametrizedTestEndEvent) {}

}
