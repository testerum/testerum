package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import java.time.LocalDateTime

data class ParametrizedTestStartEvent @JsonCreator constructor(
        @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")     override val eventKey: String,
        @JsonProperty("testName")     val testName: String,
        @JsonProperty("testFilePath") val testFilePath: Path,
        @JsonProperty("tags")         val tags: List<String>
): RunnerEvent
