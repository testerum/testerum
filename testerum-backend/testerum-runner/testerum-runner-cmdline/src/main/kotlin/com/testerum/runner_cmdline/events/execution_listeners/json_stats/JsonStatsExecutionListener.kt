package com.testerum.runner_cmdline.events.execution_listeners.json_stats

import com.testerum.common_kotlin.writeText
import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.builder.EventListenerProperties
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner_cmdline.events.execution_listeners.json_stats.model.JsonStatistics
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.BaseReportModelExecutionListener
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class JsonStatsExecutionListener (private val properties: Map<String, String>) : BaseExecutionListener() {

    private val destinationFile: JavaPath = run {
        val destinationFileNameProperty = properties[EventListenerProperties.JsonStats.DESTINATION_FILE_NAME]
                ?: throw IllegalArgumentException("missing required property ${EventListenerProperties.JsonStats.DESTINATION_FILE_NAME} for the output format ${OutputFormat.JSON_STATS}")

        return@run Paths.get(destinationFileNameProperty).toAbsolutePath().normalize()
    }

    override fun onSuiteEnd(event: SuiteEndEvent) {
        val statistics = JsonStatistics(
                executionStatus = event.status,
                durationMillis = event.durationMillis
        )

        val serializedStatistics: String = BaseReportModelExecutionListener.OBJECT_MAPPER.writeValueAsString(statistics)
        destinationFile.writeText(serializedStatistics)
    }

}
