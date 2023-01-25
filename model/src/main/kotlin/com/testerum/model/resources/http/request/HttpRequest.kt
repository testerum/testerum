package com.testerum.model.resources.http.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.resources.http.request.enums.HttpRequestMethod
import com.testerum.model.resources.http.response.ValidHttpResponse

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class HttpRequest @JsonCreator constructor(
        @JsonProperty("method") val method: HttpRequestMethod,
        @JsonProperty("url") val url: String,
        @JsonProperty("headers") val headers: Map<String, String> = emptyMap(),
        @JsonProperty("body") val body: HttpRequestBody?,
        @JsonProperty("followRedirects") val followRedirects: Boolean = true
) {

    fun getFirstHeaderValue(headerName: String): String? {
        val lowerCaseHeaderName = headerName.lowercase()

        return headers.entries.find {it.key.lowercase() == lowerCaseHeaderName}
                ?.value
    }

    fun getContentTypeHeaderValue(): String?
            = getFirstHeaderValue("Content-Type")

    @get:JsonIgnore
    val jsonBody: Any by lazy(mode = LazyThreadSafetyMode.NONE) {
        if (body == null) {
            emptyMap<String, Any>()
        } else {
            ValidHttpResponse.OBJECT_MAPPER.readValue(body.content, Object::class.java) as Any
        }
    }

}
