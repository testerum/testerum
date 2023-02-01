package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.runner.tree.id.RunnerIdCreator
import java.time.LocalDateTime

data class SuiteStartEvent @JsonCreator constructor(
        @JsonProperty("time")           override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("executionName")  val executionName: String?
): RunnerEvent {

    @get:JsonProperty("eventKey")
    override val eventKey = RunnerIdCreator.getRootId()
}
