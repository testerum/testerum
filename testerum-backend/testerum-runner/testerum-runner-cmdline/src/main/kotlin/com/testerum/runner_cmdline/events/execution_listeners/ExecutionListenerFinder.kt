package com.testerum.runner_cmdline.events.execution_listeners

import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ExecutionListenerFinder(private val executionListenersByOutputFormat: Map<CmdlineParams.OutputFormat, ExecutionListener>) {

    private val lock = ReentrantLock()
    private var _executionListeners: List<ExecutionListener>? = null

    fun setOutputFormats(outputFormats: List<CmdlineParams.OutputFormat>) {
        lock.withLock {
            if (_executionListeners != null) {
                throw throw IllegalStateException("execution listeners already set")
            }

            val executionListeners: List<ExecutionListener> = outputFormats.map {
                executionListenersByOutputFormat[it]
                        ?: throw throw IllegalStateException("could not find execution listener for output format [$it]")
            }

            _executionListeners = executionListeners
        }
    }

    val executionListeners: List<ExecutionListener>
        get() {
            lock.withLock {
                return _executionListeners
                        ?: throw IllegalStateException("execution listeners not yet set")
            }
        }

}