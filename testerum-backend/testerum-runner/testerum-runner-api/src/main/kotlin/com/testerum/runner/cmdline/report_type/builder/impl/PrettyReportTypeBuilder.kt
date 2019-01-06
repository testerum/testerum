package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder
import java.nio.file.Path

class PrettyReportTypeBuilder {

    var destinationDirectory: Path? = null

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.PRETTY)

        destinationDirectory?.let {
            builder.properties[EventListenerProperties.Pretty.DESTINATION_DIRECTORY] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}
