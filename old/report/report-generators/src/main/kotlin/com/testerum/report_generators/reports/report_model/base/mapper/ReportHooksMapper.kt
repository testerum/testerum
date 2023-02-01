package com.testerum.report_generators.reports.report_model.base.mapper

import com.testerum.report_generators.reports.report_model.base.logger.ReportToFileLogger
import com.testerum.runner.events.model.HooksEndEvent
import com.testerum.runner.events.model.HooksStartEvent
import com.testerum.runner.report_model.ReportHooks
import com.testerum.runner.report_model.ReportStep
import java.nio.file.Path as JavaPath

object ReportHooksMapper {

    fun mapReportHooks(startEvent: HooksStartEvent,
                      endEvent: HooksEndEvent,
                      destinationDirectory: JavaPath,
                      hooksLogger: ReportToFileLogger,
                      children: List<ReportStep>): ReportHooks {
        return ReportHooks(
                hookPhase = startEvent.hookPhase,
                startTime = startEvent.time,
                endTime = endEvent.time,
                durationMillis = endEvent.durationMillis,
                status = endEvent.status,
                textLogFilePath = destinationDirectory.relativize(hooksLogger.textFilePath).toString(),
                modelLogFilePath = destinationDirectory.relativize(hooksLogger.modelFilePath).toString(),
                children = children
        )
    }

}
