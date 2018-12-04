package com.testerum.runner_cmdline.events.execution_listeners

import com.testerum.runner.cmdline.OutputFormat
import com.testerum.runner.cmdline.OutputFormatParser
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.execution_listener.ExecutionListenerFactory
import com.testerum.runner_cmdline.events.execution_listeners.json_events.JsonEventsExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ExecutionListenerFinder(private val executionListenerFactories: Map<OutputFormat, ExecutionListenerFactory>) {

    private val lock = ReentrantLock()
    private var _executionListeners: List<ExecutionListener>? = null

    fun setOutputFormats(outputFormatsWithProperties: List<String>) {
        lock.withLock {
            if (_executionListeners != null) {
                throw throw IllegalStateException("execution listeners already set")
            }

            _executionListeners = outputFormatsWithProperties.map {
                createExecutionListener(it)
            }
        }
    }

    val executionListeners: List<ExecutionListener>
        get() {
            lock.withLock {
                return _executionListeners
                        ?: throw IllegalStateException("execution listeners not yet set")
            }
        }

    private fun createExecutionListener(outputFormatWithProperties: String): ExecutionListener {
        val (outputFormat, properties) = OutputFormatParser.parse(outputFormatWithProperties)

        return when (outputFormat) {
            OutputFormat.TREE -> TreeToConsoleExecutionListener()
            OutputFormat.JSON_EVENTS -> JsonEventsExecutionListener(properties)
        }
    }

}
