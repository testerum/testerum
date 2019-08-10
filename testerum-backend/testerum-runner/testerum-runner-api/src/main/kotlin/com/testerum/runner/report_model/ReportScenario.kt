package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.model.test.scenario.Scenario
import java.time.LocalDateTime

data class ReportScenario(@JsonProperty("testName")         val testName: String,
                          @JsonProperty("testFilePath")     val testFilePath: String,
                          @JsonProperty("scenario")         val scenario: Scenario,
                          @JsonProperty("scenarioIndex")    val scenarioIndex: Int,
                          @JsonProperty("tags")             val tags: List<String>,
                          @JsonProperty("startTime")        val startTime: LocalDateTime,
                          @JsonProperty("endTime")          val endTime: LocalDateTime,
                          @JsonProperty("durationMillis")   val durationMillis: Long,
                          @JsonProperty("status")           val status: ExecutionStatus,
                          @JsonProperty("textLogFilePath")  override val textLogFilePath: String,
                          @JsonProperty("modelLogFilePath") override val modelLogFilePath: String,
                          @JsonProperty("children")         val children: List<ReportStep>) : FeatureOrTestRunnerReportNode
