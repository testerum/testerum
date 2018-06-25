package com.testerum.runner.events.model.statistics

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ExecutionStatistics @JsonCreator constructor(
        @JsonProperty("failedTestsCount")     val failedTestsCount: Int,
        @JsonProperty("successfulTestsCount") val successfulTestsCount: Int,
        @JsonProperty("totalTestsCount")      val totalTestsCount: Int
)
