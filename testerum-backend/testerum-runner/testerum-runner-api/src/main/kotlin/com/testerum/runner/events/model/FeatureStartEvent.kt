package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

data class FeatureStartEvent @JsonCreator constructor(
        @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")     override val eventKey: EventKey,
        @JsonProperty("featureName")  val featureName: String,
        @JsonProperty("tags")         val tags: List<String>
): RunnerEvent
