package com.testerum.model.resources.http.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpResponseHeader @JsonCreator constructor(@JsonProperty("key") var key: String,
                                                       @JsonProperty("values") var values: List<String>)
