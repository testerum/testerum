package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.runner.events.model.error.ExceptionDetail
import java.time.LocalDateTime

data class ReportFeature(@JsonProperty("featureName")      val featureName: String,
                         @JsonProperty("startTime")        val startTime: LocalDateTime,
                         @JsonProperty("endTime")          val endTime: LocalDateTime,
                         @JsonProperty("durationMillis")   val durationMillis: Long,
                         @JsonProperty("status")           val status: ExecutionStatus,
                         @JsonProperty("exceptionDetail")  val exceptionDetail: ExceptionDetail? = null,
                         @JsonProperty("textLogFilePath")  override val textLogFilePath: String,
                         @JsonProperty("modelLogFilePath") override val modelLogFilePath: String,
                         @JsonProperty("children")         val children: List<FeatureOrTestRunnerReportNode>) : FeatureOrTestRunnerReportNode {

    @JsonProperty("exceptionDetailAsString")
    fun exceptionDetailAsString(): String? {
        return exceptionDetail?.detailedToString()
    }

}
