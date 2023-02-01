package com.testerum.report_generators.reports.report_model.base.mapper

import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.report_model.ReportScenario
import com.testerum.runner.report_model.ReportStep
import com.testerum.report_generators.reports.report_model.base.logger.ReportToFileLogger
import java.nio.file.Path as JavaPath

object ReportScenarioMapper {

    fun mapReportScenario(startEvent: ScenarioStartEvent,
                          endEvent: ScenarioEndEvent,
                          destinationDirectory: JavaPath,
                          testLogger: ReportToFileLogger,
                          children: List<ReportStep>): ReportScenario {
        return ReportScenario(
                testName = startEvent.testName,
                testFilePath = startEvent.testFilePath.toString(),
                scenario = startEvent.scenario,
                scenarioIndex = startEvent.scenarioIndex,
                tags = startEvent.tags,
                startTime = startEvent.time,
                endTime = endEvent.time,
                durationMillis = endEvent.durationMillis,
                status = endEvent.status,
                textLogFilePath = destinationDirectory.relativize(testLogger.textFilePath).toString(),
                modelLogFilePath = destinationDirectory.relativize(testLogger.modelFilePath).toString(),
                children = children
        )
    }

}
