package com.testerum.runner_cmdline.logger

import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner_cmdline.events.EventsService
import java.time.LocalDateTime

class TesterumLoggerImpl(private val eventsService: EventsService): TesterumLogger {

    override fun warn(message: String, exception: Throwable?) {
        eventsService.logEvent(
                TextLogEvent(
                        time = LocalDateTime.now(),
                        eventKey = EventKey.LOG_EVENT_KEY,
                        logLevel = LogLevel.WARNING,
                        message = message,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) }
                )
        )
    }

    override fun info(message: String, exception: Throwable?) {
        eventsService.logEvent(
                TextLogEvent(
                        time = LocalDateTime.now(),
                        eventKey = EventKey.LOG_EVENT_KEY,
                        logLevel = LogLevel.INFO,
                        message = message,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) }
                )
        )
    }

    override fun debug(message: String, exception: Throwable?) {
        eventsService.logEvent(
                TextLogEvent(
                        time = LocalDateTime.now(),
                        eventKey = EventKey.LOG_EVENT_KEY,
                        logLevel = LogLevel.DEBUG,
                        message = message,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) }
                )
        )
    }

}
