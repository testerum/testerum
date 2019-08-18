package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum.model.step.StepCall
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class StepEndEvent @JsonCreator constructor(
        @JsonProperty("time")            override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")        override val eventKey: EventKey,
        @JsonProperty("stepCall")        val stepCall: StepCall,
        @JsonProperty("status")          val status: ExecutionStatus,
        @JsonProperty("durationMillis")  val durationMillis: Long
): RunnerEvent
