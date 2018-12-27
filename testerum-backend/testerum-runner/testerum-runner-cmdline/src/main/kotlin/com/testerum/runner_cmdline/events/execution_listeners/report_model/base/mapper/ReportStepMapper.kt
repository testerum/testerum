package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.mapper

import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.report_model.ReportStep
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.logger.ReportToFileLogger
import java.nio.file.Path

object ReportStepMapper {

    fun mapReportStep(startEvent: StepStartEvent,
                      endEvent: StepEndEvent,
                      destinationDirectory: Path,
                      stepLogger: ReportToFileLogger,
                      children: List<ReportStep>,
                      stepDefsByMinId: StepDefsByMinId): ReportStep {
        val stepCall = startEvent.stepCall
        val reportStepCall = ReportStepDefAndCallMapper.mapStepCall(stepCall, stepDefsByMinId)

        return ReportStep(
                stepCall = reportStepCall,
                startTime = startEvent.time,
                endTime = endEvent.time,
                durationMillis = endEvent.durationMillis,
                status = endEvent.status,
                textLogFilePath = destinationDirectory.relativize(stepLogger.textFilePath).toString(),
                modelLogFilePath = destinationDirectory.relativize(stepLogger.modelFilePath).toString(),
                children = children
        )
    }

}
