package http_support.logging

import com.testerum.common_httpclient.util.MediaTypeUtils
import com.testerum.common_json.util.prettyPrintJson
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.ValidHttpResponse
import http.request.HttpRequestSteps
import http.response.verify.model.HttpBodyVerifyMatchingType
import http.response.verify.model.HttpResponseVerify
import org.apache.commons.lang3.StringUtils
import org.apache.http.impl.EnglishReasonPhraseCatalog
import org.slf4j.LoggerFactory
import java.util.Locale

private val LOG = LoggerFactory.getLogger(HttpRequestSteps::class.java)

fun HttpRequest.prettyPrint(): String {
    var response = "\t$method $url\n"

    for ((headerName, headerValue) in headers) {
        response += "\t$headerName: $headerValue\n"
    }

    val body = this.body
    if (body?.bodyType != null) {
        response += "\n"
        response += "\tBody type: ${body.bodyType}\n"
    }

    if (body?.content?.isNotEmpty() == true) {
        response += "\n"
        response += formatHttpBody(
                body = body.content,
                contentType = getContentTypeHeaderValue().orEmpty(),
                bodyDescription = "HTTP request body"
        )
    }

    return response
}

fun ValidHttpResponse.prettyPrint(): String {
    var response = "\t$protocol ${statusCode.prettyPrintHttpStatusCode()}\n"
    for (header in headers) {
        for (value in header.values) {
            response += "\t${header.key}: $value\n"
        }
    }

    val contentTypeHeader = headers.find {
        it.key.equals("Content-Type", ignoreCase = true)
    }

    val contentType = contentTypeHeader
            ?.values
            ?.firstOrNull()
            .orEmpty()
    if (body.isNotEmpty()) {
        response += "\n"
        response += formatHttpBody(
                body = bodyAsUtf8String,
                contentType = contentType,
                bodyDescription = "HTTP response body"
        )
    }

    return response
}

fun HttpResponseVerify.prettyPrint() = buildString {
    // status code
    val expectedStatusCode = expectedStatusCode
    if (expectedStatusCode != null) {
        append("\tExpected status code: ${expectedStatusCode.prettyPrintHttpStatusCode()}\n")
    }

    // headers
    val expectedHeaders = expectedHeaders ?: emptyList()
    if (expectedHeaders.isNotEmpty()) {
        append("\n")
        append("\tExpected headers\n")
        val longestKeyLength = expectedHeaders.map { it.key?.length ?: 0 }.max() ?: 0
        val longestCompareModeLength = expectedHeaders.map { it.compareMode?.name?.length ?: 0}.max() ?: 0
        val longestValueLength = expectedHeaders.map { it.value?.length ?: 0}.max() ?: 0

        for (header in expectedHeaders) {
            append("\t")
            append(StringUtils.rightPad(header.key, longestKeyLength))
            append("  ")
            append(StringUtils.rightPad(header.compareMode.toString(), longestCompareModeLength))
            append("  ")
            append(StringUtils.rightPad(header.value.toString(), longestValueLength))
            append("\n")
        }
    }

    // body
    val expectedBody = expectedBody
    if (expectedBody != null) {
        append("\n")
        append("\tExpected body matching type: ${expectedBody.httpBodyVerifyMatchingType}\n")
        val bodyVerify = expectedBody.bodyVerify
        if (bodyVerify != null) {
            val contentType = if (expectedBody.httpBodyVerifyMatchingType == HttpBodyVerifyMatchingType.JSON_VERIFY) {
                "application/json"
            } else {
                ""
            }

            append("\n")
            append("\tExpected body\n")
            append(
                    formatHttpBody(
                            body = bodyVerify,
                            contentType = contentType,
                            bodyDescription = "Expected body"
                    )
            )
        }
    }
    append("\n")
    append("\n")
}

fun Int.prettyPrintHttpStatusCode(): String {
    val reason = EnglishReasonPhraseCatalog.INSTANCE.getReason(this, Locale.ENGLISH)

    val reasonPostfix = if (reason == null) {
        ""
    } else {
        " $reason"
    }

    return this.toString() + reasonPostfix
}

private fun formatHttpBody(body: String,
                           contentType: String,
                           bodyDescription: String): String {
    // remove things not related to media type; examples:
    // Content-Type: text/html; charset=utf-8
    // Content-Type: multipart/form-data; boundary=something
    val mediaType = contentType.substringBefore(";").trim()

    return if (MediaTypeUtils.isJsonMediaType(mediaType)) {
        try {
            val serializedBody = body.prettyPrintJson()

            serializedBody.lines()
                    .joinToString(separator = "\n") {
                        "\t" + it
                    }
        } catch (e: Exception) {
            LOG.warn("failed to format $bodyDescription as JSON: it's not valid JSON; contentType=[$contentType])")

            body
        }
    } else {
        body.lines()
                .joinToString(separator = "\n") {
                    "\t" + it
                }
    }
}
