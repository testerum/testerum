package com.testerum.model.resources.http.mock.stub

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.resources.http.mock.stub.enums.HttpMockFaultResponse
import com.testerum.model.resources.http.mock.stub.request.HttpMockRequest
import com.testerum.model.resources.http.mock.stub.response.HttpMockProxyResponse
import com.testerum.model.resources.http.mock.stub.response.HttpMockResponse

data class HttpMock @JsonCreator constructor(
        @JsonProperty("expectedRequest") var expectedRequest: HttpMockRequest,
        @JsonProperty("mockResponse") var mockResponse: HttpMockResponse?,
        @JsonProperty("faultResponse") var faultResponse: HttpMockFaultResponse?,
        @JsonProperty("proxyResponse") var proxyResponse: HttpMockProxyResponse?
)