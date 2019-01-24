package com.testerum.model.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Message  @JsonCreator constructor(@JsonProperty("key") val key: MessageKey,
                                             @JsonProperty("value") val value: String)