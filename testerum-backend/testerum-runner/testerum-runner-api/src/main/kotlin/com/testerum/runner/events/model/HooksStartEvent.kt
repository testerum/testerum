package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.feature.hooks.HookPhase
import java.time.LocalDateTime

data class HooksStartEvent @JsonCreator constructor(
    @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
    @JsonProperty("eventKey")     override val eventKey: String,
    @JsonProperty("hookPhase")    val hookPhase: HookPhase
): RunnerEvent
