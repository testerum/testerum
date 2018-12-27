package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.mapper

import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.report_model.FeatureOrTestRunnerReportNode
import com.testerum.runner.report_model.ReportFeature
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.logger.ReportToFileLogger
import java.nio.file.Path as JavaPath

object ReportFeatureMapper {

    fun mapReportFeature(startEvent: FeatureStartEvent,
                         endEvent: FeatureEndEvent,
                         destinationDirectory: JavaPath,
                         featureLogger: ReportToFileLogger,
                         children: List<FeatureOrTestRunnerReportNode>): ReportFeature {
        return ReportFeature(
                featureName = startEvent.featureName,
                tags = startEvent.tags,
                startTime = startEvent.time,
                endTime = endEvent.time,
                durationMillis = endEvent.durationMillis,
                status = endEvent.status,
                textLogFilePath = destinationDirectory.relativize(featureLogger.textFilePath).toString(),
                modelLogFilePath = destinationDirectory.relativize(featureLogger.modelFilePath).toString(),
                children = children.toList()
        )
    }

}
