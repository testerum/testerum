package net.qutester.model.resources.http.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class HttpResponse @JsonCreator constructor(
        @JsonProperty("protocol") val protocol: String,
        @JsonProperty("statusCode") val statusCode: Int,
        @JsonProperty("headers") val headers: List<HttpResponseHeader> = emptyList(),
        @JsonProperty("body") val body: ByteArray = ByteArray(0) // todo: serialize for client to base64 (reduce size)
) {
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