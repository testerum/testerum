package com.testerum.runner_cmdline.events.execution_listeners

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.parser.OutputFormatParser
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.execution_listener.ExecutionListenerFactory
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

    fun getExecutionListenersSafely(): List<ExecutionListener> {
        lock.withLock {
            return _executionListeners ?: emptyList()
        }
    }

    private fun createExecutionListener(outputFormatWithProperties: String): ExecutionListener {
        val (outputFormat, properties) = OutputFormatParser.parse(outputFormatWithProperties)

        val createExecutionListener = executionListenerFactories[outputFormat]
                ?: throw IllegalArgumentException("cannot find an execution listener for output format [$outputFormat]")

        return createExecutionListener(properties)
    }

}
