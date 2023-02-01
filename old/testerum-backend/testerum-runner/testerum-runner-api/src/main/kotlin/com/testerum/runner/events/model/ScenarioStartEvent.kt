package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.scenario.Scenario
import java.time.LocalDateTime

data class ScenarioStartEvent @JsonCreator constructor(
        @JsonProperty("time")          override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")      override val eventKey: String,
        @JsonProperty("testName")      val testName: String,
        @JsonProperty("testFilePath")  val testFilePath: Path,
        @JsonProperty("scenario")      val scenario: Scenario,
        @JsonProperty("scenarioIndex") val scenarioIndex: Int,
        @JsonProperty("tags")          val tags: List<String>
): RunnerEvent
