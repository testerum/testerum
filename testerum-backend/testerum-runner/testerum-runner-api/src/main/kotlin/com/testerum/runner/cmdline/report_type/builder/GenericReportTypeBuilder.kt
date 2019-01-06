package com.testerum.runner.cmdline.report_type.builder

import com.testerum.runner.cmdline.report_type.RunnerReportType
import java.nio.file.Path as JavaPath

class GenericReportTypeBuilder(private val teportType: RunnerReportType) {

    companion object {
        private val ESCAPE_PROPERTY_REGEX = Regex("([,=])")
        private val ESCAPE_PROPERTY_REPLACEMENT = "\\\\$1"
    }

    val properties = LinkedHashMap<String, String?>()

    fun build(): String = buildString {
        append(teportType.name)

        if (properties.isNotEmpty()) {
            append(':')

            var first = true
            for ((key, value) in properties) {
                if (first) {
                    first = false
                } else {
                    append(',')
                }

                append(key.escapeProperty())
                if (value != null) {
                    append("=")
                    append(value.escapeProperty())
                }
            }
        }

    }

    private fun String.escapeProperty() = this.replace(ESCAPE_PROPERTY_REGEX, ESCAPE_PROPERTY_REPLACEMENT)
}

