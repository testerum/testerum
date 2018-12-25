package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.logger

import com.testerum.runner.report_model.ReportLog
import java.util.*
import java.nio.file.Path as JavaPath

class ReportToFileLoggerStack {

    private val stack = ArrayDeque<ReportToFileLogger>()

    fun push(textFilePath: JavaPath,
             modelFilePath: JavaPath) {
        stack.addLast(
                ReportToFileLogger(textFilePath, modelFilePath)
        )
    }

    fun log(logEvent: ReportLog) {
        for (logger in stack) {
            logger.log(logEvent)
        }
    }

    fun pop(): ReportToFileLogger {
        val logger = stack.removeLast()

        logger.close()

        return logger
    }

}
