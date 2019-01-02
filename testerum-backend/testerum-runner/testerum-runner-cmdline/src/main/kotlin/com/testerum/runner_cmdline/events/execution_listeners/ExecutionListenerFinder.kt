package com.testerum.runner_cmdline.events.execution_listeners

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.marshaller.OutputFormatParser
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.execution_listener.ExecutionListenerFactory
import com.testerum.runner_cmdline.events.execution_listeners.report_model.template.ManagedReportsExecutionListener
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import java.nio.file.Path as JavaPath

class ExecutionListenerFinder(private val executionListenerFactories: Map<OutputFormat, ExecutionListenerFactory>,
                              private val managedReportsExecutionListenerFactory: (managedReportsDir: JavaPath) -> ManagedReportsExecutionListener) {

    private val lock = ReentrantLock()
    private var _executionListeners: List<ExecutionListener>? = null

    fun setOutputFormats(outputFormatsWithProperties: List<String>,
                         managedReportsDir: JavaPath?) {
        lock.withLock {
            if (_executionListeners != null) {
                throw throw IllegalStateException("execution listeners already set")
            }

            _executionListeners = createExecutionListeners(outputFormatsWithProperties, managedReportsDir)
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

    private fun createExecutionListeners(outputFormatsWithProperties: List<String>,
                                         managedReportsDir: JavaPath?): List<ExecutionListener> {
        val result = ArrayList<ExecutionListener>()

        // create from output formats
        for (outputFormatWithProperties in outputFormatsWithProperties) {
            result += createExecutionListener(outputFormatWithProperties)
        }

        // create from managedReportsDir
        if (managedReportsDir != null) {
            result += managedReportsExecutionListenerFactory(managedReportsDir)
        }

        return result
    }

    private fun createExecutionListener(outputFormatWithProperties: String): ExecutionListener {
        val (outputFormat, properties) = OutputFormatParser.parse(outputFormatWithProperties)

        val createExecutionListener = executionListenerFactories[outputFormat]
                ?: throw IllegalArgumentException("cannot find an execution listener for output format [$outputFormat]")

        return createExecutionListener(properties)
    }

}
