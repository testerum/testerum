package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder
import java.nio.file.Path

class CustomTemplateReportTypeBuilder {

    var templateDirectory: Path? = null

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.CUSTOM_TEMPLATE)

        templateDirectory?.let {
            builder.properties[EventListenerProperties.CustomTemplate.TEMPLATE_DIRECTORY] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}
