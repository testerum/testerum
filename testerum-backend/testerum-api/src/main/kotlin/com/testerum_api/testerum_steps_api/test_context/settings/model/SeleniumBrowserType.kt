package com.testerum_api.testerum_steps_api.test_context.settings.model

enum class SeleniumBrowserType {

    CHROME,
    FIREFOX,
    OPERA,
    EDGE,
    INTERNET_EXPLORER,
    SAFARI,
    REMOTE,
    ;

    companion object {
        fun safeValueOf(name: String): SeleniumBrowserType? {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
