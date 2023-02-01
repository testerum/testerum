package com.testerum.runner.cmdline.report_type.model.json_stats

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

data class JsonStatistics @JsonCreator constructor(@JsonProperty("executionStatus") val executionStatus: ExecutionStatus,
                                                   @JsonProperty("durationMillis")  val durationMillis: Long)
