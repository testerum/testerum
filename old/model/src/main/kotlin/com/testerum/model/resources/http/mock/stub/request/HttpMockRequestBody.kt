package com.testerum.model.resources.http.mock.stub.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.resources.http.mock.stub.enums.HttpMockRequestBodyMatchingType
import com.testerum.model.resources.http.mock.stub.enums.HttpMockRequestBodyVerifyType

data class HttpMockRequestBody @JsonCreator constructor(
        @JsonProperty("matchingType") var matchingType: HttpMockRequestBodyMatchingType,
        @JsonProperty("bodyType") var bodyType: HttpMockRequestBodyVerifyType,
        @JsonProperty("content") var content: String
)