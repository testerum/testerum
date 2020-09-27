package com.testerum.report_server.controller

import com.testerum.report_server.service.ReportService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/report/v1", produces = ["application/json"])
class ReportController(private val reportService: ReportService) {

    @PostMapping
    fun addReport(@RequestBody events: String) {
        val eventsList = events.lines()
        reportService.addReport(eventsList)
    }
}
