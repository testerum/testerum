package com.testerum.runner_cmdline.events.execution_listeners

import com.testerum.report_generators.reports.report_model.template.ManagedReportsExecutionListener
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.marshaller.RunnerReportTypeParser
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.execution_listener.ExecutionListenerFactory
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import java.nio.file.Path as JavaPath

class ExecutionListenerFinder(private val executionListenerFactories: Map<RunnerReportType, ExecutionListenerFactory>,
                              private val managedReportsExecutionListenerFactory: (managedReportsDir: JavaPath) -> ManagedReportsExecutionListener) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExecutionListenerFinder::class.java)
    }

    private val lock = ReentrantLock()
    private var _executionListeners: List<ExecutionListener>? = null

    fun setReports(reportsWithProperties: List<String>,
                   managedReportsDir: JavaPath?) {
        LOGGER.info("setting reports $reportsWithProperties; managedReportsDir=[$managedReportsDir]")
        lock.withLock {
            if (_executionListeners != null) {
                throw throw IllegalStateException("execution listeners already set")
            }

            _executionListeners = createExecutionListeners(reportsWithProperties, managedReportsDir)
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

    private fun createExecutionListeners(reportsWithProperties: List<String>,
                                         managedReportsDir: JavaPath?): List<ExecutionListener> {
        val result = ArrayList<ExecutionListener>()

        // create from reports
        for (reportWithProperties in reportsWithProperties) {
            result += createExecutionListener(reportWithProperties)
        }

        // create from managedReportsDir
        if (managedReportsDir != null) {
            result += managedReportsExecutionListenerFactory(managedReportsDir)
        }

        LOGGER.info("executionListeners=[${result.map { it.javaClass.name }}]")

        return result
    }

    private fun createExecutionListener(reportWithProperties: String): ExecutionListener {
        val (reportType, properties) = RunnerReportTypeParser.parse(reportWithProperties)

        val createExecutionListener = executionListenerFactories[reportType]
                ?: throw IllegalArgumentException("cannot find an execution listener for report type [$reportType]")

        return createExecutionListener(properties)
    }

}
