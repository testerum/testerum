package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

data class TestStartEvent @JsonCreator constructor(
        @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")     override val eventKey: EventKey,
        @JsonProperty("testName")     val testName: String,
        @JsonProperty("testFilePath") val testFilePath: Path
): RunnerEvent
