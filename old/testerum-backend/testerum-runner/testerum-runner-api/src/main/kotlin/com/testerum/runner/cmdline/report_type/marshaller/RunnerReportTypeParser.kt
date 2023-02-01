package com.testerum.runner.cmdline.report_type.marshaller

import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.marshaller.properties.RunnerPropertiesParser

object RunnerReportTypeParser {

    fun parse(reportWithProperties: String): Pair<RunnerReportType, Map<String, String>> {
        val indexOfColon = reportWithProperties.indexOf(":")

        val reportTypeString: String
        val propertiesString: String

        if (indexOfColon == -1) {
            reportTypeString = reportWithProperties
            propertiesString = ""
        } else {
            reportTypeString = reportWithProperties.substring(0, indexOfColon)
            propertiesString = reportWithProperties.substring(indexOfColon + 1)
        }

        val reportType = RunnerReportType.parse(reportTypeString)
        val properties = RunnerPropertiesParser.parse(propertiesString)

        return Pair(reportType, properties)
    }

}
