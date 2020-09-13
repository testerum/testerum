package com.testerum.runner_cmdline.events

import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.report_generators.reports.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EventsService(private val executionListenerFinder: ExecutionListenerFinder) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RunnerListenersModuleFactory::class.java)
    }

    fun start() {
        for (executionListener in executionListenerFinder.executionListeners) {
            executionListener.start()
        }
    }

    fun logEvent(runnerEvent: RunnerEvent) {
        informEventListenersOfConsoleLogging()
        informEventListeners(runnerEvent)
    }

    private fun informEventListenersOfConsoleLogging() {
        val capturedText = ConsoleOutputCapturer.drainCapturedText()

        if (capturedText.isEmpty()) {
            return
        }

        val lines = capturedText.lines()
        val events = lines.map { TextLogEvent(LocalDateTime.now(), EventKey.LOG_EVENT_KEY, LogLevel.INFO, it, null) }

        for (event in events) {
            informEventListeners(event)
        }
    }

    private fun informEventListeners(runnerEvent: RunnerEvent) {
        for (executionListener in executionListenerFinder.executionListeners) {
            executionListener.onEvent(runnerEvent)
        }
    }

    fun stop() {
        for (executionListener in executionListenerFinder.getExecutionListenersSafely()) {
            try {
                executionListener.stop()
            } catch (e: Exception) {
                LOG.error("failed to shutdown execution listener ${executionListener.javaClass.simpleName}", e)
            }
        }
    }
}
