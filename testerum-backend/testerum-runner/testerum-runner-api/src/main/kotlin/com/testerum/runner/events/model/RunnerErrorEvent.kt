package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

/*
 * This event is never emitted by the runner.
 * It is only used to communicate between the backend and the frontend.
 */
data class RunnerErrorEvent @JsonCreator constructor(
        @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("errorMessage") val errorMessage: String
): RunnerEvent {

    @get:JsonProperty("eventKey")
    override val eventKey: EventKey
        get() = EventKey.SUITE_EVENT_KEY

}
