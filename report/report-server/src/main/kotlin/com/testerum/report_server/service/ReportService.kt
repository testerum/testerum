package com.testerum.report_server.service

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.report_generators.reports.report_model.template.ManagedReportsExecutionListener
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.report_generators.reports.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner.events.model.ConfigurationEvent
import com.testerum.runner.events.model.RunnerEvent
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths

@Service
class ReportService() {

    init {
        //TODO Ionut: Cristi, what is this?
        ConsoleOutputCapturer.startCapture("main")
    }


    fun addReport(eventsList: List<String>) {
        val configEvent: ConfigurationEvent = getConfigEvent(eventsList)

        val reportPath = getReportPath(configEvent)

        val managedReportsExecutionListener = ManagedReportsExecutionListener(reportPath)

        managedReportsExecutionListener.start();
        for (eventAsString in eventsList) {
            val event = EXECUTION_LISTENERS_OBJECT_MAPPER.readValue<RunnerEvent>(eventAsString)
            managedReportsExecutionListener.onEvent(event)
        }
        managedReportsExecutionListener.stop()
    }

    private fun getReportPath(configEvent: ConfigurationEvent): Path {
        //TODO Ionut: to speak with Cristi what is the best option
        val rootReportDirectory = "C:/temp/testerum-server-reports"
        val executedEnvironment = configEvent.variablesEnvironment ?: "default-environment"
        val projectIdentifier = "${executedEnvironment}-${configEvent.projectId}"
        val reportsPathAsStringBuilder = "${rootReportDirectory}/${configEvent.projectName}/$projectIdentifier"

        return Paths.get(reportsPathAsStringBuilder)
    }

    private fun getConfigEvent(eventsList: List<String>): ConfigurationEvent {
        //TODO Ionut: this event finding is not nice, what should we do
        val configEventAsString: String = eventsList.first { it.startsWith("{\"@type\":\"CONFIGURATION_EVENT\"") }
        return EXECUTION_LISTENERS_OBJECT_MAPPER.readValue<ConfigurationEvent>(configEventAsString)
    }
}
