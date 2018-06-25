package net.qutester.model.resources.http.mock.stub.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.resources.http.mock.stub.enums.HttpMockRequestParamsCompareMode

data class HttpMockRequestParam @JsonCreator constructor(
        @JsonProperty("key") var key: String,
        @JsonProperty("compareMode") var compareMode: HttpMockRequestParamsCompareMode,
        @JsonProperty("value") var value: String?
)