package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.feature.hooks.HookPhase
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import java.time.LocalDateTime

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class HooksEndEvent @JsonCreator constructor(
    @JsonProperty("time")            override val time: LocalDateTime = LocalDateTime.now(),
    @JsonProperty("eventKey")        override val eventKey: String,
    @JsonProperty("hookPhase")       val hookPhase: HookPhase,
    @JsonProperty("status")          val status: ExecutionStatus,
    @JsonProperty("durationMillis")  val durationMillis: Long
): RunnerEvent
