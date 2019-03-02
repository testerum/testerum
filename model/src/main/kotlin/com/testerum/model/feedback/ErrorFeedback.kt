package com.testerum.model.feedback

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorFeedback @JsonCreator constructor(@JsonProperty("contactName") val contactName: String?,
                                                  @JsonProperty("contactEmail") val contactEmail: String?,
                                                  @JsonProperty("stepsToReproduce") val stepsToReproduce: String?,
                                                  @JsonProperty("errorMessage") val errorMessage: String?,
                                                  @JsonProperty("errorStacktrace") val errorStacktrace: String?)
