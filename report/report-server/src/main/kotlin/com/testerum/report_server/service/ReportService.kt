package com.testerum.report_server.service

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.report_generators.reports.report_model.template.ManagedReportsExecutionListener
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.events.model.ConfigurationEvent
import com.testerum.runner.events.model.RunnerEvent
import org.springframework.stereotype.Service
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

@Service
class ReportService() {

    fun addReport(eventsList: List<String>) {
        val configEvent: ConfigurationEvent = getConfigEvent(eventsList)

        val reportPath = getReportPath(configEvent)

        val managedReportsExecutionListener = ManagedReportsExecutionListener(reportPath)

        try {
            managedReportsExecutionListener.start();
            for (eventAsString in eventsList) {
                val event = EXECUTION_LISTENERS_OBJECT_MAPPER.readValue<RunnerEvent>(eventAsString)
                managedReportsExecutionListener.onEvent(event)
            }
        } finally {
            managedReportsExecutionListener.stop()
        }
    }

    private fun getReportPath(configEvent: ConfigurationEvent): JavaPath {
        val rootReportDirectory = Paths.get(System.getProperty("user.home")).resolve(".testerum").resolve("reports")
        val executedEnvironment = configEvent.variablesEnvironment ?: "default-environment"
        val projectIdentifier = "${executedEnvironment}-${configEvent.projectId}"

        return rootReportDirectory.resolve(configEvent.projectName).resolve(projectIdentifier)
    }

    private fun getConfigEvent(eventsList: List<String>): ConfigurationEvent {
        val configEventAsString: String = eventsList.first { it.contains("\"@type\":\"CONFIGURATION_EVENT\"") }

        return EXECUTION_LISTENERS_OBJECT_MAPPER.readValue(configEventAsString)
    }
}
