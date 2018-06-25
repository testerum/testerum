package net.qutester.model.resources.http.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.resources.http.request.enums.HttpRequestBodyType

data class HttpRequestBody @JsonCreator constructor(
        @JsonProperty("bodyType") val bodyType: HttpRequestBodyType,
        @JsonProperty("content") val content: String
)