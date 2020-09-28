package com.testerum.runner.cmdline.report_type

import com.testerum.runner.cmdline.report_type.builder.ReportTypeBuilders

enum class RunnerReportType {
    CONSOLE,
    CONSOLE_DEBUG,

    JSON_EVENTS,
    JSON_MODEL,
    JSON_STATS,

    CUSTOM_TEMPLATE,
    PRETTY,

    REMOTE_SERVER
    ;

    companion object {
        val VALID_VALUES = values()
                .toList()
                .joinToString(separator = ", ", prefix = "", postfix = "") { "[$it]" }

        fun parse(text: String): RunnerReportType {
            for (reportType in values()) {
                if (reportType.name == text) {
                    return reportType
                }
            }

            throw IllegalArgumentException("there is no report type [$text]; valid values are: $VALID_VALUES")
        }

        fun builders() = ReportTypeBuilders
    }
}
