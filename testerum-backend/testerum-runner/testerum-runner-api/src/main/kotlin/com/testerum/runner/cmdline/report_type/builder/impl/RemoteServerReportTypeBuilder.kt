package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties.RemoteServer.REPORT_SERVER_URL
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder

class RemoteServerReportTypeBuilder {

    var reportServerUrl: String? = null

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.REMOTE_SERVER)

        reportServerUrl?.let {
            builder.properties[REPORT_SERVER_URL] = it
        }

        return builder.build()
    }
}
