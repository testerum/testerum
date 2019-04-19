package com.testerum.model.feedback

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Feedback @JsonCreator constructor(@JsonProperty("email") val email: String?,
                                             @JsonProperty("subject") val subject: String?,
                                             @JsonProperty("description") val description: String?)
