package com.testerum.common_profiles

enum class TesterumProfile {

    DEV,
    PROD,
    ;

    companion object {
        val DEFAULT: TesterumProfile = PROD

        fun safeValueOf(value: String): TesterumProfile? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

    }

}
