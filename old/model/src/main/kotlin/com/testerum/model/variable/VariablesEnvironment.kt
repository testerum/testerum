package com.testerum.model.variable

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class VariablesEnvironment @JsonCreator constructor(@JsonProperty("name") val name: String,
                                                         @JsonProperty("variables") val variables: List<Variable>)