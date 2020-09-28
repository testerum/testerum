package com.testerum.report_server.model

data class ReportInfo(
    val projectName: String,
    val projectEnvironment: String,
    val projectId: String,
    val statisticsReportPath: String,
    val latestReportPath: String,
    val autoRefreshDashboardPath: String
)
