package com.testerum.runner_cmdline.module_di.submodules

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.runner.cmdline.OutputFormat
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.events.execution_listeners.json_events.JsonEventsExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener
import org.slf4j.LoggerFactory

class RunnerListenersModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RunnerListenersModuleFactory::class.java)
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    val executionListenerFinder = ExecutionListenerFinder(
            mapOf(
                    OutputFormat.TREE to { properties: Map<String, String> -> TreeToConsoleExecutionListener() },
                    OutputFormat.JSON_EVENTS to { properties: Map<String, String> -> JsonEventsExecutionListener(properties) }
            )
    ).apply {
        context.registerShutdownHook {
            for (executionListener in executionListeners) {
                try {
                    executionListener.stop()
                } catch (e: Exception) {
                    LOG.error("failed to shutdown execution listener ${executionListener.javaClass.simpleName}", e)
                }
            }
        }
    }

}
