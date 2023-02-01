package com.testerum.model.test.scenario.param

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ScenarioParam @JsonCreator constructor(@JsonProperty("name") val name: String,
                                                  @JsonProperty("type") val type: ScenarioParamType,
                                                  @JsonProperty("value") val value: String)