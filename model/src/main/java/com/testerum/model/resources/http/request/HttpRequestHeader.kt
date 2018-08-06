package com.testerum.model.resources.http.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpRequestHeader @JsonCreator constructor(@JsonProperty("key") val key: String,
                                                      @JsonProperty("value") val value: String)
