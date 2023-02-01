package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class FeatureStartEvent @JsonCreator constructor(
        @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")     override val eventKey: String,
        @JsonProperty("featureName")  val featureName: String,
        @JsonProperty("tags")         val tags: List<String>
): RunnerEvent
