package com.testerum.model.resources.http.mock.stub.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpMockResponse @JsonCreator constructor(
        @JsonProperty("statusCode") var statusCode: Int,
        @JsonProperty("headers") var headers: Map<String, String> = emptyMap(),
        @JsonProperty("body") var body: HttpMockResponseBody?,
        @JsonProperty("delay") var delay: Int?
)