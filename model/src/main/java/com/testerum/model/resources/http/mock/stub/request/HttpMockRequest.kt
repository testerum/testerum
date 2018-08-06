package com.testerum.model.resources.http.mock.stub.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.resources.http.mock.stub.enums.HttpMockRequestMethod

data class HttpMockRequest @JsonCreator constructor(
        @JsonProperty("method") var method: HttpMockRequestMethod,
        @JsonProperty("url") var url: String,
        @JsonProperty("params") var params: List<HttpMockRequestParam>? = emptyList(),
        @JsonProperty("headers") var headers: List<HttpMockRequestHeader>? = emptyList(),
        @JsonProperty("body") var body: HttpMockRequestBody?,
        @JsonProperty("scenario") var scenario: HttpMockRequestScenario?
)