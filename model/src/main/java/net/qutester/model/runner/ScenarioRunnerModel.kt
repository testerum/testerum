package net.qutester.model.runner

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.RunningStatusEnum

data class ScenarioRunnerModel @JsonCreator constructor(
        @JsonProperty("id") val id: String,
        @JsonProperty("testModel") val testModel: List<String>,
        @JsonProperty("status") val status: RunningStatusEnum,
        @JsonProperty("logs") val logs: List<String>
)