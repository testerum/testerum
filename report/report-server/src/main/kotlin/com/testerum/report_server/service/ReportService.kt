package com.testerum.report_server.service

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.report_generators.reports.report_model.template.ManagedReportsExecutionListener
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.events.model.RunnerEvent
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class ReportService {

    fun addReport(eventsList: List<String>) {
        val managedReportsExecutionListener = ManagedReportsExecutionListener(Paths.get("C:/temp/testerum-server-reports"))

        managedReportsExecutionListener.start();
        for (eventAsString in eventsList) {
            val event = EXECUTION_LISTENERS_OBJECT_MAPPER.readValue<RunnerEvent>(eventAsString)
            managedReportsExecutionListener.onEvent(event)
        }
        managedReportsExecutionListener.stop()
    }
}
