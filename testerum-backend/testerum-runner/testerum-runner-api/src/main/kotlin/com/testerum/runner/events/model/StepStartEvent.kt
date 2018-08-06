package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.step.StepCall
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

data class StepStartEvent @JsonCreator constructor(
        @JsonProperty("time")     override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey") override val eventKey: EventKey,
        @JsonProperty("stepCall") val stepCall: StepCall
): RunnerEvent
