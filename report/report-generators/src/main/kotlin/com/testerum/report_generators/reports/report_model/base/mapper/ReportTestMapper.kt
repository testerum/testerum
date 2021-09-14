package com.testerum.report_generators.reports.report_model.base.mapper

import com.testerum.report_generators.reports.report_model.base.logger.ReportToFileLogger
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.report_model.ReportTest
import com.testerum.runner.report_model.RunnerReportNode
import java.nio.file.Path as JavaPath

object ReportTestMapper {

    fun mapReportTest(startEvent: TestStartEvent,
                      endEvent: TestEndEvent,
                      destinationDirectory: JavaPath,
                      testLogger: ReportToFileLogger,
                      children: List<RunnerReportNode>): ReportTest {
        return ReportTest(
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
