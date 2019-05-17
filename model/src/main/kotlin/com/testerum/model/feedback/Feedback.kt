package com.testerum.model.feedback

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Feedback @JsonCreator constructor(@JsonProperty("name") val name: String?,
                                             @JsonProperty("email") val email: String?,
                                             @JsonProperty("message") val message: String)
