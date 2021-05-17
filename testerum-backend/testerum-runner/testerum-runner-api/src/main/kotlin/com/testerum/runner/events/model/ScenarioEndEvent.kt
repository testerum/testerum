package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.scenario.Scenario
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import java.time.LocalDateTime

data class ScenarioEndEvent @JsonCreator constructor(
        @JsonProperty("time")           override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")       override val eventKey: String,
        @JsonProperty("testName")       val testName: String,
        @JsonProperty("testFilePath")   val testFilePath: Path,
        @JsonProperty("scenario")       val scenario: Scenario,
        @JsonProperty("scenarioIndex")  val scenarioIndex: Int,
        @JsonProperty("status")         val status: ExecutionStatus,
        @JsonProperty("durationMillis") val durationMillis: Long
): RunnerEvent
