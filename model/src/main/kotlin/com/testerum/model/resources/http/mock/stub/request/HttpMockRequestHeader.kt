package com.testerum.model.resources.http.mock.stub.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.resources.http.mock.stub.enums.HttpMockRequestHeadersCompareMode

data class HttpMockRequestHeader @JsonCreator constructor(
        @JsonProperty("key") var key: String,
        @JsonProperty("compareMode") var compareMode: HttpMockRequestHeadersCompareMode,
        @JsonProperty("value") var value: String?
)