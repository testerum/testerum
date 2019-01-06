package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder
import java.nio.file.Path

class JsonStatsReportTypeBuilder {

    var destinationFileName: Path? = null

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.JSON_STATS)

        destinationFileName?.let {
            builder.properties[EventListenerProperties.JsonStats.DESTINATION_FILE_NAME] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}
