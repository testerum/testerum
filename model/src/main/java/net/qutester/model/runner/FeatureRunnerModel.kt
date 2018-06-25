package net.qutester.model.runner

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.RunningStatusEnum

data class FeatureRunnerModel @JsonCreator constructor(
        @JsonProperty("id") val id: String?,
        @JsonProperty("status") val status: RunningStatusEnum = RunningStatusEnum.WAITING_TO_RUN,
        @JsonProperty("scenarios") val scenarios: List<ScenarioRunnerModel>,
        @JsonProperty("logs") val logs: List<String>
)