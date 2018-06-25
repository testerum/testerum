package com.testerum.runner.logger

import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.runner.events.EventsService
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

class TesterumLoggerImpl(private val eventsService: EventsService): TesterumLogger {

    override fun logWarning(message: String) {
        eventsService.logEvent(
                TextLogEvent(LocalDateTime.now(), EventKey.LOG_EVENT_KEY, LogLevel.WARNING, message)
        )
    }

    override fun logInfo(message: String) {
        eventsService.logEvent(
                TextLogEvent(LocalDateTime.now(), EventKey.LOG_EVENT_KEY, LogLevel.INFO, message)
        )
    }

    override fun logDebug(message: String) {
        eventsService.logEvent(
                TextLogEvent(LocalDateTime.now(), EventKey.LOG_EVENT_KEY, LogLevel.DEBUG, message)
        )
    }
}