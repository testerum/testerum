package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.runner.events.model.log_level.LogLevel
import java.time.LocalDateTime

data class ReportLog @JsonCreator constructor(@JsonProperty("time")     val time: LocalDateTime,
                                              @JsonProperty("logLevel") val logLevel: LogLevel = LogLevel.INFO,
                                              @JsonProperty("message")  val message: String)
