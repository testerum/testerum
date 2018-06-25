package net.qutester.model.variable

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


data class Variable @JsonCreator constructor(
        @JsonProperty("key") val key: String,
        @JsonProperty("value") val value: String
)