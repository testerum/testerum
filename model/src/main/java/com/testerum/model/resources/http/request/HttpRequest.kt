package com.testerum.model.resources.http.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.resources.http.request.enums.HttpRequestMethod

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class HttpRequest @JsonCreator constructor(
        @JsonProperty("method") val method: HttpRequestMethod,
        @JsonProperty("url") val url: String,
        @JsonProperty("headers") val headers: Map<String, String> = emptyMap(),
        @JsonProperty("body") val body: HttpRequestBody?
) {

    fun getFirstHeaderValue(headerName: String): String? {
        val lowerCaseHeaderName = headerName.toLowerCase()

        return headers.entries.find {it.key.toLowerCase() == lowerCaseHeaderName}
                ?.value
    }

    fun getContentTypeHeaderValue(): String?
            = getFirstHeaderValue("Content-Type")

}