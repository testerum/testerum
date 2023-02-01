package com.testerum.common.json_diff.impl.compare_mode

enum class JsonCompareMode(val code: String) {

    EXACT("exact"),
    UNORDERED_EXACT("unorderedExact"), // ignores array item order
    CONTAINS("contains"),
    ;

    companion object {
        fun parse(code: String?): JsonCompareMode {
            if (code == null) {
                throw invalidValue(code)
            }

            for (value in values()) {
                if (code == value.code) {
                    return value
                }
            }

            throw invalidValue(code)
        }

        private fun invalidValue(code: String?) = IllegalArgumentException("[$code] is not a valid compare mode; use one of ${validValues()}")

        private fun validValues() = values().map { it.code }
    }

}