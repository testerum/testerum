package com.testerum.runner_cmdline.module_di.submodules

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.runner.cmdline.EventListenerProperties
import com.testerum.runner.cmdline.OutputFormat
import com.testerum.runner.events.execution_listener.ExecutionListenerFactory
import com.testerum.runner_cmdline.dirs.RunnerDirs
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.events.execution_listeners.json_events.JsonEventsExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.report_model.json_model.JsonModelExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.report_model.template.custom_template.CustomTemplateExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class RunnerListenersModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RunnerListenersModuleFactory::class.java)
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    val executionListenerFinder = ExecutionListenerFinder(
            mapOf(
                    OutputFormat.TREE            to { properties: Map<String, String> -> TreeToConsoleExecutionListener() },

                    OutputFormat.JSON_EVENTS     to { properties: Map<String, String> -> JsonEventsExecutionListener(properties) },
                    OutputFormat.JSON_MODEL      to { properties: Map<String, String> -> JsonModelExecutionListener(properties) },

                    OutputFormat.CUSTOM_TEMPLATE to { properties: Map<String, String> -> CustomTemplateExecutionListener(properties) },
                    OutputFormat.BASIC_HTML      to builtInTemplateExecutionListenerFactory("basic_html")
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

    private fun builtInTemplateExecutionListenerFactory(name: String): ExecutionListenerFactory = { properties: Map<String, String> ->
        val scriptFileName: JavaPath = RunnerDirs.getReportTemplatesDir()
                .resolve(name)
                .resolve("main.bundle.js")
                .toAbsolutePath().normalize()

        val propertiesIncludingScriptFile = LinkedHashMap<String, String>(properties)
        propertiesIncludingScriptFile[EventListenerProperties.CustomTemplate.SCRIPT_FILE] = scriptFileName.toString()

        CustomTemplateExecutionListener(propertiesIncludingScriptFile)
    }

}
