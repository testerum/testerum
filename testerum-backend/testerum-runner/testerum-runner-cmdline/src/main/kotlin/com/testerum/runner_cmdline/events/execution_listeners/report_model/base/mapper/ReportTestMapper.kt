package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.mapper

import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.report_model.ReportStep
import com.testerum.runner.report_model.ReportTest
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.logger.ReportToFileLogger
import java.nio.file.Path as JavaPath

object ReportTestMapper {

    fun mapReportTest(startEvent: TestStartEvent,
                      endEvent: TestEndEvent,
                      destinationDirectory: JavaPath,
                      testLogger: ReportToFileLogger,
                      children: List<ReportStep>): ReportTest {
        return ReportTest(
                testName = startEvent.testName,
                testFilePath = startEvent.testFilePath.toString(),
                tags = startEvent.tags,
                startTime = startEvent.time,
                endTime = endEvent.time,
                durationMillis = endEvent.durationMillis,
                status = endEvent.status,
                exceptionDetail = endEvent.exceptionDetail,
                textLogFilePath = destinationDirectory.relativize(testLogger.textFilePath).toString(),
                modelLogFilePath = destinationDirectory.relativize(testLogger.modelFilePath).toString(),
                children = children
        )
    }

}
