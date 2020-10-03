package com.testerum.report_generators.reports.json_stats

import com.testerum.common_kotlin.writeText
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.model.json_stats.JsonStatistics
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.SuiteEndEvent
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class JsonStatsExecutionListener (private val properties: Map<String, String>) : BaseExecutionListener() {

    private val destinationFile: JavaPath = run {
        val destinationFileNameProperty = properties[EventListenerProperties.JsonStats.DESTINATION_FILE_NAME]
                ?: throw IllegalArgumentException("missing required property ${EventListenerProperties.JsonStats.DESTINATION_FILE_NAME} for the report type [${RunnerReportType.JSON_STATS}]")

        return@run Paths.get(destinationFileNameProperty).toAbsolutePath().normalize()
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        val statistics = JsonStatistics(
                executionStatus = event.status,
                durationMillis = event.durationMillis
        )

        val serializedStatistics: String = EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(statistics)
        destinationFile.writeText(serializedStatistics)
    }

}
