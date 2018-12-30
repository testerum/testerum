package com.testerum.runner.cmdline.output_format

import com.testerum.runner.cmdline.output_format.builder.OutputFormatBuilders

enum class OutputFormat {
    CONSOLE_DEBUG,

    JSON_EVENTS,
    JSON_MODEL,
    JSON_STATS,

    CUSTOM_TEMPLATE,
    PRETTY,
    ;

    companion object {
        val VALID_OUTPUT_FORMATS = values()
                .toList()
                .joinToString(separator = ", ", prefix = "", postfix = "") { "[$it]" }

        fun parse(text: String): OutputFormat {
            for (outputFormat in values()) {
                if (outputFormat.name == text) {
                    return outputFormat
                }
            }

            throw IllegalArgumentException("there is no output format [$text]; valid values are: $VALID_OUTPUT_FORMATS")
        }

        fun builders() = OutputFormatBuilders
    }
}
