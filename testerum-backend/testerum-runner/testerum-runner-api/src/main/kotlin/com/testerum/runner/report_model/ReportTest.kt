package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import java.time.LocalDateTime

data class ReportTest(@JsonProperty("testName")         val testName: String,
                      @JsonProperty("testFilePath")     val testFilePath: String,
                      @JsonProperty("tags")             val tags: List<String>,
                      @JsonProperty("startTime")        val startTime: LocalDateTime,
                      @JsonProperty("endTime")          val endTime: LocalDateTime,
                      @JsonProperty("durationMillis")   val durationMillis: Long,
                      @JsonProperty("status")           val status: ExecutionStatus,
                      @JsonProperty("textLogFilePath")  override val textLogFilePath: String,
                      @JsonProperty("modelLogFilePath") override val modelLogFilePath: String,
                      @JsonProperty("children")         val children: List<RunnerReportNode>) : FeatureOrTestRunnerReportNode
