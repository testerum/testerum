package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class FeatureEndEvent @JsonCreator constructor(
        @JsonProperty("time")            override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")        override val eventKey: EventKey,
        @JsonProperty("featureName")     val featureName: String,
        @JsonProperty("status")          val status: ExecutionStatus,
        @JsonProperty("durationMillis")  val durationMillis: Long
): RunnerEvent
