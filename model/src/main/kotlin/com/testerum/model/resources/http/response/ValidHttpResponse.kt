package com.testerum.model.resources.http.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ValidHttpResponse @JsonCreator constructor(
        @JsonProperty("protocol") val protocol: String,
        @JsonProperty("statusCode") val statusCode: Int,
        @JsonProperty("headers") val headers: List<HttpResponseHeader> = emptyList(),
        @JsonProperty("body") val body: ByteArray = ByteArray(0)
) : HttpResponse {

    companion object {
        val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    @get:JsonIgnore
    val bodyAsUtf8String: String by lazy(mode = LazyThreadSafetyMode.NONE) {
        String(body, Charsets.UTF_8)
    }

    @get:JsonIgnore
    val jsonBody: Any by lazy(mode = LazyThreadSafetyMode.NONE) {
        OBJECT_MAPPER.readValue(bodyAsUtf8String, Object::class.java)
    }

    fun getHeaderValue(key: String): String? {
        for (header in headers) {
            if (header.key.equals(key, true)) {
                if( header.values.isNotEmpty() )return header.values[0];
            }
        }

        return null;
    }



}
