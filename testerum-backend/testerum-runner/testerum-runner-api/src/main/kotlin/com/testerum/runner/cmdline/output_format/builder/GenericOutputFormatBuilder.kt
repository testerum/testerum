package com.testerum.runner.cmdline.output_format.builder

import com.testerum.runner.cmdline.output_format.OutputFormat
import java.nio.file.Path as JavaPath

class GenericOutputFormatBuilder(private val outputFormat: OutputFormat) {

    companion object {
        private val ESCAPE_PROPERTY_REGEX = Regex("([,=])")
        private val ESCAPE_PROPERTY_REPLACEMENT = "\\\\$1"
    }

    val properties = LinkedHashMap<String, String?>()

    fun build(): String = buildString {
        append(outputFormat.name)

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

