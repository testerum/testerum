package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.ExecutionStatus
import java.time.LocalDateTime

data class ReportSuite @JsonCreator constructor(@JsonProperty("startTime")      val startTime: LocalDateTime,
                                                @JsonProperty("endTime")        val endTime: LocalDateTime,
                                                @JsonProperty("durationMillis") val durationMillis: Long,
                                                @JsonProperty("status")         val status: ExecutionStatus,
                                                @JsonProperty("logs")           override val logs: List<ReportLog>,
                                                @JsonProperty("children")       val children: List<FeatureOrTestRunnerReportNode>) : RunnerReportNode
