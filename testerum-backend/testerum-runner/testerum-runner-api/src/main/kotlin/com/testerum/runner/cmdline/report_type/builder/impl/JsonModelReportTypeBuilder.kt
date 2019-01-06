package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder
import java.nio.file.Path

class JsonModelReportTypeBuilder {

    var destinationFileDirectory: Path? = null
    var formatted: Boolean? = null

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.JSON_MODEL)

        destinationFileDirectory?.let {
            builder.properties[EventListenerProperties.JsonModel.DESTINATION_DIRECTORY] = it.toAbsolutePath().normalize().toString()
        }

        formatted?.let {
            builder.properties[EventListenerProperties.JsonModel.FORMATTED] = it.toString()
        }

        return builder.build()
    }
}
