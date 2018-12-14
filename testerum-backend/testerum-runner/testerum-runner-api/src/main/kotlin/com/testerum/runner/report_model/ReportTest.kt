package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.model.infrastructure.path.Path
import com.testerum.runner.events.model.error.ExceptionDetail
import java.time.LocalDateTime

data class ReportTest(@JsonProperty("testName")        val testName: String,
                      @JsonProperty("testFilePath")    val testFilePath: Path,
                      @JsonProperty("startTime")       val startTime: LocalDateTime,
                      @JsonProperty("endTime")         val endTime: LocalDateTime,
                      @JsonProperty("durationMillis")  val durationMillis: Long,
                      @JsonProperty("status")          val status: ExecutionStatus,
                      @JsonProperty("exceptionDetail") val exceptionDetail: ExceptionDetail? = null,
                      @JsonProperty("logs")            override val logs: List<ReportLog>,
                      @JsonProperty("children")        val children: List<ReportStep>) : FeatureOrTestRunnerReportNode {

    @JsonProperty("exceptionDetailAsString")
    fun exceptionDetailAsString(): String? {
        return exceptionDetail?.detailedToString()
    }

}
