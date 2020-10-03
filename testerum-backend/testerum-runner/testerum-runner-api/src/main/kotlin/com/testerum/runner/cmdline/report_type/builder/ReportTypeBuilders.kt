package com.testerum.runner.cmdline.report_type.builder

import com.testerum.runner.cmdline.report_type.builder.impl.ConsoleDebugReportTypeBuilder
import com.testerum.runner.cmdline.report_type.builder.impl.CustomTemplateReportTypeBuilder
import com.testerum.runner.cmdline.report_type.builder.impl.JsonEventsReportTypeBuilder
import com.testerum.runner.cmdline.report_type.builder.impl.JsonModelReportTypeBuilder
import com.testerum.runner.cmdline.report_type.builder.impl.JsonStatsReportTypeBuilder
import com.testerum.runner.cmdline.report_type.builder.impl.PrettyReportTypeBuilder
import com.testerum.runner.cmdline.report_type.builder.impl.RemoteServerReportTypeBuilder

// facade to make it easy to access all builders using auto-completion
object ReportTypeBuilders {

    fun consoleDebug(body: ConsoleDebugReportTypeBuilder.() -> Unit): String {
        val builder = ConsoleDebugReportTypeBuilder()

        builder.body()

        return builder.build()
    }

    fun jsonEvents(body: JsonEventsReportTypeBuilder.() -> Unit): String {
        val builder = JsonEventsReportTypeBuilder()

        builder.body()

        return builder.build()
    }

    fun jsonModel(body: JsonModelReportTypeBuilder.() -> Unit): String {
        val builder = JsonModelReportTypeBuilder()

        builder.body()

        return builder.build()
    }

    fun jsonStats(body: JsonStatsReportTypeBuilder.() -> Unit): String {
        val builder = JsonStatsReportTypeBuilder()

        builder.body()

        return builder.build()
    }

    fun customTemplate(body: CustomTemplateReportTypeBuilder.() -> Unit): String {
        val builder = CustomTemplateReportTypeBuilder()

        builder.body()

        return builder.build()
    }

    fun pretty(body: PrettyReportTypeBuilder.() -> Unit): String {
        val builder = PrettyReportTypeBuilder()

        builder.body()

        return builder.build()
    }

    fun remoteServer(body: RemoteServerReportTypeBuilder.() -> Unit): String {
        val builder = RemoteServerReportTypeBuilder()

        builder.body()

        return builder.build()
    }
}
