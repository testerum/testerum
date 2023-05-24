package com.testerum.runner_cmdline.module_di

object TesterumRunnerLoggingConfigurator {

    private enum class LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        OFF
    }

    fun configureLogging() {
        val overallConfig = mapOf(
                "defaultLogLevel"  to "info",
                "showDateTime"     to "true",
                "dateTimeFormat"   to "HH:mm:ss.SSS",
                "showThreadName"   to "true",
                "showLogName"      to "true",
                "showShortLogName" to "false"
        )
        val loggersConfig = mapOf(
                "org.reflections.Reflections" to LogLevel.ERROR
        )

        overallConfig.forEach(this::applyOverallLogConfigOption)
        loggersConfig.forEach(this::applyLoggersLogConfigOption)
    }

    private fun applyOverallLogConfigOption(key: String, value: String) {
        val fullKey = "org.slf4j.simpleLogger.$key"

        setPropertyIfNotAlreadySet(fullKey, value)
    }

    private fun applyLoggersLogConfigOption(loggerName: String,
                                            logLevel: LogLevel) {
        val fullKey = "org.slf4j.simpleLogger.log.$loggerName"
        val shadedFullKey = "org.slf4j.simpleLogger.log.com.testerum.shaded.$loggerName"

        setPropertyIfNotAlreadySet(fullKey, logLevel.name.lowercase())
        setPropertyIfNotAlreadySet(shadedFullKey, logLevel.name.lowercase())
    }

    private fun setPropertyIfNotAlreadySet(key: String, value: String) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value)
        }
    }

}
