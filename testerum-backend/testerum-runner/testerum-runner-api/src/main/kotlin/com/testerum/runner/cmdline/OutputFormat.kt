package com.testerum.runner.cmdline

enum class OutputFormat {
    CONSOLE_DEBUG,

    JSON_EVENTS,
    JSON_MODEL,

    CUSTOM_TEMPLATE,
    BASIC_HTML,
    STEPS_TAG_OVERVIEW,
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
    }
}
