package http_support.logging

import com.testerum.common_httpclient.util.MediaTypeUtils
import com.testerum.common_json.util.prettyPrintJson
import com.testerum.model.resources.http.mock.stub.HttpMock
import com.testerum.model.resources.http.mock.stub.enums.HttpMockRequestBodyMatchingType
import com.testerum.model.resources.http.mock.stub.enums.HttpMockRequestBodyVerifyType
import com.testerum.model.resources.http.mock.stub.enums.HttpMockResponseBodyType
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.ValidHttpResponse
import http.response.verify.model.HttpBodyVerifyMatchingType
import http.response.verify.model.HttpResponseVerify
import org.apache.commons.lang3.StringUtils
import org.apache.http.impl.EnglishReasonPhraseCatalog
import java.util.*

fun HttpRequest.prettyPrint() = buildString {
    append("\t$method $url\n")

    val longestNameLength = headers.map { it.key.length }.max() ?: 0
    if (headers.isNotEmpty()) {
        append("\n")
        for ((headerName, headerValue) in headers) {
            append("\t")
            append(StringUtils.rightPad(headerName, longestNameLength))
            append(" : ")
            append(headerValue)
            append("\n")
        }
    }

    val body = body
    if (body?.bodyType != null) {
        append("\n")
        append("\tBody type: ${body.bodyType}\n")
    }

    val bodyContent = body?.content
    if (bodyContent?.isNotEmpty() == true) {
        append("\n")
        append(
                formatHttpBody(
                        body = bodyContent,
                        contentType = getContentTypeHeaderValue().orEmpty(),
                        bodyDescription = "HTTP request body"
                )
        )
    }
    append("\n")
    append("\n")
}

fun ValidHttpResponse.prettyPrint() = buildString {
    append("\t$protocol ${statusCode.prettyPrintHttpStatusCode()}\n")

    if (headers.isNotEmpty()) {
        append("\n")
        val longestNameLength = headers.map { it.key.length }.max() ?: 0
        for (header in headers) {
            for (value in header.values) {
                append("\t")
                append(StringUtils.rightPad(header.key, longestNameLength))
                append(" : ")
                append(value)
                append("\n")
            }
        }
    }

    if (body.isNotEmpty()) {
        val contentTypeHeader = headers.find {
            it.key.equals("Content-Type", ignoreCase = true)
        }

        val contentType = contentTypeHeader
                ?.values
                ?.firstOrNull()
                .orEmpty()

        append("\n")
        append(
                formatHttpBody(
                        body = bodyAsUtf8String,
                        contentType = contentType,
                        bodyDescription = "HTTP response body"
                )
        )
    }

    append("\n")
    append("\n")

    append("Response duration in millis: $durationInMillis")
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

        for (header in expectedHeaders) {
            append("\t")
            append(StringUtils.rightPad(header.key, longestKeyLength))
            append("  ")
            append(StringUtils.rightPad(header.compareMode.toString(), longestCompareModeLength))
            append("  ")
            append(header.value)
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

fun HttpMock.prettyPrint() = buildString {
    // expected request
    append("\tExpected Request\n")
    append("\t================\n")
    append("\n")
    append("\t${expectedRequest.method} ${expectedRequest.url}\n")

    // query params
    val expectedQueryParams = expectedRequest.params
    if (expectedQueryParams?.isNotEmpty() == true) {
        append("\n")
        append("\tExpected query params\n")
        append("\t---------------------\n")

        val longestKeyLength = expectedQueryParams.map { it.key.length }.max() ?: 0
        val longestCompareModeLength = expectedQueryParams.map { it.compareMode.name.length }.max() ?: 0
        for (queryParam in expectedQueryParams) {
            append("\t")
            append(StringUtils.rightPad(queryParam.key, longestKeyLength))
            append("  ")
            append(StringUtils.rightPad(queryParam.compareMode.name, longestCompareModeLength))
            if (queryParam.value != null) {
                append("  ")
                append(queryParam.value)
            }
            append("\n")
        }
    }

    // headers
    val expectedHeaders = expectedRequest.headers
    if (expectedHeaders?.isNotEmpty() == true) {
        append("\n")
        append("\tExpected headers\n")
        append("\t----------------\n")

        val longestKeyLength = expectedHeaders.map { it.key.length }.max() ?: 0
        val longestCompareModeLength = expectedHeaders.map { it.compareMode.name.length }.max() ?: 0
        for (header in expectedHeaders) {
            append("\t")
            append(StringUtils.rightPad(header.key, longestKeyLength))
            append("  ")
            append(StringUtils.rightPad(header.compareMode.name, longestCompareModeLength))
            if (header.value != null) {
                append("  ")
                append(header.value)
            }
            append("\n")
        }
    }

    // body
    val expectedBody = expectedRequest.body
    if (expectedBody != null) {
        append("\n")
        append("\tExpected body\n")
        append("\t-------------\n")
        append("\n")
        append("\tMatching type: ${expectedBody.matchingType}\n")

        append("\n")
        append("\tBody type: ${expectedBody.bodyType}\n")

        val isJsonBody = (expectedBody.matchingType == HttpMockRequestBodyMatchingType.JSON_VERIFY)
                || (expectedBody.bodyType == HttpMockRequestBodyVerifyType.JSON)
        val isXml = expectedBody.bodyType == HttpMockRequestBodyVerifyType.XML
        val isHtml = expectedBody.bodyType == HttpMockRequestBodyVerifyType.HTML
        val contentType = when {
            isJsonBody -> "application/json"
            isXml      -> "application/xml"
            isHtml     -> "text/html"
            else       -> ""
        }

        val bodyContent = expectedBody.content
        append("\n")
        append(
                formatHttpBody(
                        body = bodyContent,
                        contentType = contentType,
                        bodyDescription = "Expected body"
                )
        )
        append("\n")
    }

    // scenario
    val scenario = expectedRequest.scenario
    if (scenario != null) {
        append("\n")
        append("\tScenario\n")
        append("\t--------\n")
        append("\tname           : ${scenario.scenarioName}\n")
        append("\trequired state : ${scenario.currentState}\n")
        append("\tnew state      : ${scenario.newState}\n")
    }

    // mock response
    val mockResponse = mockResponse
    if (mockResponse != null) {
        append("\n")
        append("\n")
        append("\tMock response\n")
        append("\t=============\n")

        // status code
        append("\n")
        append("\t${mockResponse.statusCode.prettyPrintHttpStatusCode()}\n")

        // headers
        if (mockResponse.headers.isNotEmpty()) {
            append("\n")
            val longestNameLength = mockResponse.headers.map { it.key.length }.max() ?: 0
            for ((key, value) in mockResponse.headers) {
                append("\t")
                append(StringUtils.rightPad(key, longestNameLength))
                append(" : ")
                append(value)
                append("\n")
            }
        }

        // body
        val body = mockResponse.body
        if (body != null) {
            append("\n")
            append("\tBody type: ${body.bodyType}\n")

            val isJsonBody = body.bodyType == HttpMockResponseBodyType.JSON
            val isXml = body.bodyType == HttpMockResponseBodyType.XML
            val isJavaScript = body.bodyType == HttpMockResponseBodyType.JAVASCRIPT
            val isHtml = body.bodyType == HttpMockResponseBodyType.HTML
            val contentType = when {
                isJsonBody   -> "application/json"
                isXml        -> "application/xml"
                isJavaScript -> "text/javascript"
                isHtml       -> "text/html"
                else         -> ""
            }

            val bodyContent = body.content
            append("\n")
            append(
                    formatHttpBody(
                            body = bodyContent,
                            contentType = contentType,
                            bodyDescription = "Response body"
                    )
            )
        }

        // delay
        val mockResponseDelay = mockResponse.delay
        if (mockResponseDelay != null) {
            append("\n")
            append("\tMock Response delay: $mockResponseDelay\n")
        }
    }

    // fault response
    val faultResponse = faultResponse
    if (faultResponse != null) {
        append("\n")
        append("\n")
        append("\tFault response: ${faultResponse}\n")
    }

    // proxy response
    val proxyResponse = proxyResponse
    if (proxyResponse != null) {
        append("\n")
        append("\n")
        append("\tProxy response base URL: ${proxyResponse.proxyBaseUrl}\n")
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
                           bodyDescription: String): String {  // todo: bodyDescription is unused; check how we need to use it (check where it's used, and how the logs look with it)
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
            body
        }
    } else {
        body.lines()
                .joinToString(separator = "\n") {
                    "\t" + it
                }
    }
}
