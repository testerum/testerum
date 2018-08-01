package net.qutester.model.resources.http.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class HttpResponse @JsonCreator constructor(
        @JsonProperty("protocol") val protocol: String,
        @JsonProperty("statusCode") val statusCode: Int,
        @JsonProperty("headers") val headers: List<HttpResponseHeader> = emptyList(),
        @JsonProperty("body") val body: ByteArray = ByteArray(0) // todo: serialize for client to base64 (reduce size)
) {

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
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

    val bodyAsUtf8String: String by lazy(mode = LazyThreadSafetyMode.NONE) {
        String(body, Charsets.UTF_8)
    }

    val jsonBody: Any by lazy(mode = LazyThreadSafetyMode.NONE) {
        OBJECT_MAPPER.readValue(bodyAsUtf8String, Object::class.java)
    }

    override fun toString(): String {
        var response = "$protocol $statusCode\n"
        for (header in headers) {
            for (value in header.values) {
                response += "${header.key}: $value\n"
            }
        }

        if (body.isNotEmpty()) {
            response +=  "\n"
            response += String(body);
        }

        return response
    }
}