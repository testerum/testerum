package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder
import java.nio.file.Path

class JsonEventsReportTypeBuilder {

    var reportServerURL: String? = null
    var destinationFileName: Path? = null
    var wrapJsonWithPrefixAndPostfix: Boolean? = null

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.JSON_EVENTS)

        reportServerURL?.let {
            builder.properties[EventListenerProperties.JsonEvents.REPORT_SERVER_URL] = it
        }

        destinationFileName?.let {
            builder.properties[EventListenerProperties.JsonEvents.DESTINATION_FILE_NAME] = it.toAbsolutePath().normalize().toString()
        }

        wrapJsonWithPrefixAndPostfix?.let {
            builder.properties[EventListenerProperties.JsonEvents.WRAP_JSON_WITH_PREFIX_AND_POSTFIX] = it.toString()
        }

        return builder.build()
    }
}
