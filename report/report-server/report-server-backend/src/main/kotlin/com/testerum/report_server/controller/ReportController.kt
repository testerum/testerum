package com.testerum.report_server.controller

import com.testerum.logging.getLogger
import com.testerum.report_server.model.ReportInfo
import com.testerum.report_server.service.AddReportService
import com.testerum.report_server.service.GetReportsInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/reports", produces = ["application/json"])
class ReportController(private val addReportService: AddReportService,
                       private val getReportsInfoService: GetReportsInfoService) {

    companion object {
        private val LOGGER = getLogger()
    }

    @GetMapping
    fun getReportsInfo(): List<ReportInfo> {
        return getReportsInfoService.getReportsInfo()
    }

    @PostMapping
    fun addReport(@RequestBody events: String) {
        LOGGER.info("Add Report endpoint was called")
        val eventsList = events.lines()
        addReportService.addReport(eventsList)
        LOGGER.info("Add Report endpoint has finsed")
    }
}
