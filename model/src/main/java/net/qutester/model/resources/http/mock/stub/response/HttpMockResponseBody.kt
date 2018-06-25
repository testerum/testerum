package net.qutester.model.resources.http.mock.stub.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.resources.http.mock.stub.enums.HttpMockResponseBodyType

data class HttpMockResponseBody @JsonCreator constructor(
        @JsonProperty("bodyType") var bodyType: HttpMockResponseBodyType,
        @JsonProperty("content") var content: String
)