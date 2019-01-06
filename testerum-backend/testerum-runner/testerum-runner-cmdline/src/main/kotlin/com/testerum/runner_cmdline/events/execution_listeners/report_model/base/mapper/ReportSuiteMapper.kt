package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.mapper

import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.report_model.FeatureOrTestRunnerReportNode
import com.testerum.runner.report_model.ReportSuite
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.logger.ReportToFileLogger
import java.nio.file.Path as JavaPath

object ReportSuiteMapper {

    fun mapReportSuite(startEvent: SuiteStartEvent,
                       endEvent: SuiteEndEvent,
                       destinationDirectory: JavaPath,
                       suiteLogger: ReportToFileLogger,
                       children: List<FeatureOrTestRunnerReportNode>,
                       stepDefsByMinId: StepDefsByMinId): ReportSuite {
        return ReportSuite(
                executionName = startEvent.executionName,
                startTime = startEvent.time,
                endTime = endEvent.time,
                durationMillis = endEvent.durationMillis,
                status = endEvent.status,
                textLogFilePath = destinationDirectory.relativize(suiteLogger.textFilePath).toString(),
                modelLogFilePath = destinationDirectory.relativize(suiteLogger.modelFilePath).toString(),
                children = children.toList(),
                stepDefsById = stepDefsByMinId.getMap()
        )
    }

}
