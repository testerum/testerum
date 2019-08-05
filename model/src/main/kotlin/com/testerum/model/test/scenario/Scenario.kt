package com.testerum.model.test.scenario

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.test.scenario.param.ScenarioParam

data class Scenario @JsonCreator constructor(@JsonProperty("description") val description: String? = null,
                                             @JsonProperty("params") val params: List<ScenarioParam>)