package net.qutester.model.resources.http.mock.stub.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpMockProxyResponse @JsonCreator constructor(
        @JsonProperty("proxyBaseUrl") var proxyBaseUrl: String
)