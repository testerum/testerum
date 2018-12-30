package com.testerum.runner_cmdline.events.execution_listeners.json_stats.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.ExecutionStatus

data class JsonStatistics @JsonCreator constructor(@JsonProperty("executionStatus") val executionStatus: ExecutionStatus,
                                                   @JsonProperty("durationMillis")  val durationMillis: Long)
