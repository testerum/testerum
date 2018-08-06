package com.testerum.model.resources.http.mock.stub.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpMockResponseHeader @JsonCreator constructor(@JsonProperty("key") var key: String,
                                                           @JsonProperty("value") var value: String)
