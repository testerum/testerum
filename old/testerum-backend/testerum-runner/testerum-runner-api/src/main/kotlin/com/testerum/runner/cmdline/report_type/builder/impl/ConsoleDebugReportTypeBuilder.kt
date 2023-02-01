package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder

class ConsoleDebugReportTypeBuilder {

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.CONSOLE_DEBUG)

        return builder.build()
    }

}
