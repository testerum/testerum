package com.testerum.runner.cmdline

enum class OutputFormat {
    TREE,
    JSON_EVENTS,
    ;

    companion object {
        private val validOutputFormats = values()
                .toList()
                .joinToString(separator = ", ", prefix = "", postfix = "") { "[$it]" }

        fun parse(text: String): OutputFormat {
            for (outputFormat in values()) {
                if (outputFormat.name == text) {
                    return outputFormat
                }
            }

            throw IllegalArgumentException("there is no output format [$text]; valid values are: $validOutputFormats")
        }
    }
}