package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

// todo: in what case(s) should we create this event? What does it do? It looks like an implementation detail in the TestRunnerEventProcessor and we should move/delete it from the runner-api
data class RunnerErrorEvent @JsonCreator constructor(
        @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("errorMessage") val errorMessage: String
): RunnerEvent {

    @get:JsonProperty("eventKey")
    override val eventKey: EventKey
        get() = EventKey.SUITE_EVENT_KEY

}