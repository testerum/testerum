package com.testerum.runner_cmdline.runner_tree.runner_context

import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner_cmdline.test_context.TestContextImpl
import com.testerum.runner_cmdline.transformer.TransformerFactory
import java.time.LocalDateTime

data class RunnerContext(val eventsService: EventsService,
                         val stepsClassLoader: ClassLoader,
                         val glueObjectFactory: GlueObjectFactory,
                         val transformerFactory: TransformerFactory,
                         val testVariables: TestVariablesImpl,
                         val testContext: TestContextImpl) {

    fun logEvent(runnerEvent: RunnerEvent) {
        eventsService.logEvent(runnerEvent)
    }

    fun logMessage(message: String,
                   exception: Throwable? = null) {
        val logLevel = if (exception != null) {
            if (exception is AssertionError) {
                LogLevel.WARNING
            } else {
                LogLevel.ERROR
            }
        } else {
            LogLevel.INFO
        }

        eventsService.logEvent(
                TextLogEvent(
                        time = LocalDateTime.now(),
                        eventKey = EventKey.LOG_EVENT_KEY,
                        logLevel = logLevel,
                        message = message,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) }
                )
        )
    }

}
