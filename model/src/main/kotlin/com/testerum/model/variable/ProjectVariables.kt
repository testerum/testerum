package com.testerum.model.variable

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectVariables @JsonCreator constructor(@JsonProperty("currentEnvironment") val currentEnvironment: String,
                                                     @JsonProperty("defaultVariables") val defaultVariables: List<Variable>,
                                                     @JsonProperty("localVariables") val localVariables: List<Variable>,
                                                     @JsonProperty("environments") val environments: List<VariablesEnvironment>)