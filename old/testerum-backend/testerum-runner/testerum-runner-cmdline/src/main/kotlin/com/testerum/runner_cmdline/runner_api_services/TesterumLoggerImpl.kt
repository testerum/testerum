package com.testerum.runner_cmdline.runner_api_services

import com.testerum.model.runner.tree.id.RunnerIdCreator
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner_cmdline.events.EventsService
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import java.time.LocalDateTime

class TesterumLoggerImpl(private val eventsService: EventsService) : TesterumLogger {

    override fun error(message: String, exception: Throwable?) {
        eventsService.logEvent(
            TextLogEvent(
                time = LocalDateTime.now(),
                eventKey = RunnerIdCreator.getRootId(),
                logLevel = LogLevel.ERROR,
                message = message,
                exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) }
            )
        )
    }

    override fun warn(message: String, exception: Throwable?) {
        eventsService.logEvent(
            TextLogEvent(
                time = LocalDateTime.now(),
                eventKey = RunnerIdCreator.getRootId(),
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
                eventKey = RunnerIdCreator.getRootId(),
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
                eventKey = RunnerIdCreator.getRootId(),
                logLevel = LogLevel.DEBUG,
                message = message,
                exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) }
            )
        )
    }

}
