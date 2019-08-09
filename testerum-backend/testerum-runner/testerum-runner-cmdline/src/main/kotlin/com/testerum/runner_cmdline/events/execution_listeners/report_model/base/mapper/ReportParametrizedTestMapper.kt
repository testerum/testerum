package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.mapper

import com.testerum.runner.events.model.ParametrizedTestEndEvent
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.report_model.ReportParametrizedTest
import com.testerum.runner.report_model.ReportScenario
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.logger.ReportToFileLogger
import java.nio.file.Path as JavaPath

object ReportParametrizedTestMapper {

    fun mapReportParametrizedTest(startEvent: ParametrizedTestStartEvent,
                                  endEvent: ParametrizedTestEndEvent,
                                  destinationDirectory: JavaPath,
                                  testLogger: ReportToFileLogger,
                                  children: List<ReportScenario>): ReportParametrizedTest {
        return ReportParametrizedTest(
                testName = startEvent.testName,
                testFilePath = startEvent.testFilePath.toString(),
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
