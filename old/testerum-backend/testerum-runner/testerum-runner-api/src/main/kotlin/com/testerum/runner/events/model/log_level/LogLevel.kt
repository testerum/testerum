package com.testerum.runner.events.model.log_level

enum class LogLevel(val formatForLogging: String) {

    ERROR   ("[ERROR]"),
    WARNING ("[WARN ]"),
    INFO    ("[INFO ]"),
    DEBUG   ("[DEBUG]")
    ;

}
