package com.testerum.runner_cmdline.module_di.submodules

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.report_generators.dirs.ReportDirs
import com.testerum.report_generators.reports.console.ConsoleExecutionListener
import com.testerum.report_generators.reports.console_debug.ConsoleDebugExecutionListener
import com.testerum.report_generators.reports.json_events.JsonEventsExecutionListener
import com.testerum.report_generators.reports.json_stats.JsonStatsExecutionListener
import com.testerum.report_generators.reports.remote_server.RemoteServerExecutionListener
import com.testerum.report_generators.reports.report_model.json_model.JsonModelExecutionListener
import com.testerum.report_generators.reports.report_model.template.ManagedReportsExecutionListener
import com.testerum.report_generators.reports.report_model.template.custom_template.CustomTemplateExecutionListener
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.events.execution_listener.ExecutionListenerFactory
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import java.nio.file.Path as JavaPath

class RunnerListenersModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    val executionListenerFinder = ExecutionListenerFinder(
            executionListenerFactories = mapOf(
                    RunnerReportType.CONSOLE            to { properties: Map<String, String> -> ConsoleExecutionListener() },
                    RunnerReportType.CONSOLE_DEBUG      to { properties: Map<String, String> -> ConsoleDebugExecutionListener() },

                    RunnerReportType.JSON_EVENTS        to { properties: Map<String, String> -> JsonEventsExecutionListener(properties) },
                    RunnerReportType.JSON_MODEL         to { properties: Map<String, String> -> JsonModelExecutionListener(properties) },
                    RunnerReportType.JSON_STATS         to { properties: Map<String, String> -> JsonStatsExecutionListener(properties) },

                    RunnerReportType.CUSTOM_TEMPLATE    to { properties: Map<String, String> -> CustomTemplateExecutionListener(properties) },
                    RunnerReportType.PRETTY             to builtInTemplateExecutionListenerFactory("pretty"),

                    RunnerReportType.REMOTE_SERVER      to { properties: Map<String, String> -> RemoteServerExecutionListener(properties) }

                ),
            managedReportsExecutionListenerFactory = {  managedReportsDir: JavaPath -> ManagedReportsExecutionListener(managedReportsDir) }
    )

    private fun builtInTemplateExecutionListenerFactory(name: String): ExecutionListenerFactory = { properties: Map<String, String> ->
        val scriptFileName: JavaPath = ReportDirs.getReportTemplatesDir()
                .resolve(name)
                .resolve("main.bundle.js")
                .toAbsolutePath().normalize()

        val propertiesIncludingScriptFile = LinkedHashMap<String, String>(properties)
        propertiesIncludingScriptFile[EventListenerProperties.CustomTemplate.SCRIPT_FILE] = scriptFileName.toString()

        CustomTemplateExecutionListener(propertiesIncludingScriptFile)
    }

}
