package com.testerum.runner.cmdline.report_type.builder.impl

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.GenericReportTypeBuilder

class JUnitReportTypeBuilder {

    fun build(): String {
        val builder = GenericReportTypeBuilder(RunnerReportType.JUNIT)

        return builder.build()
    }

}
