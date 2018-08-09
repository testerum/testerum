package http.response.verify

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.HttpResponse
import com.testerum.model.resources.http.response.HttpResponseHeader
import http.response.verify.model.HttpBodyVerifyMatchingType
import http.response.verify.model.HttpBodyVerifyMatchingType.*
import http.response.verify.model.HttpResponseHeaderVerify
import http.response.verify.model.HttpResponseVerify
import http.response.verify.model.HttpResponseVerifyHeadersCompareMode
import http.response.verify.model.HttpResponseVerifyHeadersCompareMode.CONTAINS
import http.response.verify.transformer.HttpResponseVerifyTransformer
import http_support.module_di.HttpStepsModuleServiceLocator
import org.slf4j.LoggerFactory

class HttpResponseVerifySteps {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(HttpResponseVerifySteps::class.java)
    }

    private val jsonComparer: JsonComparer = HttpStepsModuleServiceLocator.bootstrapper.jsonDiffModuleFactory.jsonComparer
    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Then("I expect <<httpResponseVerify>> HTTP Response")
    fun verifyHttpResponse(@Param(transformer = HttpResponseVerifyTransformer::class) httpResponseVerify: HttpResponseVerify) {
        val httpRequest: HttpRequest = variables["httpRequest"] as HttpRequest
        val httpResponse: HttpResponse = variables["httpResponse"] as HttpResponse

        LOGGER.debug("Verifying HTTP Response [\n$httpResponse\n]")

        verifyExpectedCode(httpResponseVerify, httpResponse, httpRequest)
        verifyExpectedHeaders(httpResponseVerify, httpResponse, httpRequest)
        verifyExpectedBody(httpResponseVerify, httpRequest, httpResponse)

        LOGGER.debug("Http Request executed successfully")
    }

    private fun verifyExpectedBody(httpResponseVerify: HttpResponseVerify, httpRequest: HttpRequest, httpResponse: HttpResponse) {
        val expectedBody = httpResponseVerify.expectedBody ?: return

        val matchingType: HttpBodyVerifyMatchingType = expectedBody.httpBodyVerifyMatchingType
        when (matchingType) {
            IS_EMPTY -> verifyBodyIsEmpty(httpRequest, httpResponse)
            JSON_VERIFY -> verifyBodyAsJsonVerify(httpResponseVerify, httpRequest, httpResponse)
            HttpBodyVerifyMatchingType.CONTAINS, HttpBodyVerifyMatchingType.REGEX_MATCH, EXACT_MATCH -> verifyBodyAsText(httpResponseVerify, httpRequest, httpResponse)
        }

    }

    private fun verifyBodyAsJsonVerify(httpResponseVerify: HttpResponseVerify, httpRequest: HttpRequest, httpResponse: HttpResponse) {
        val compareMode = httpResponseVerify.expectedBody?.httpBodyVerifyMatchingType ?: EXACT_MATCH

        val expectedBody = httpResponseVerify.expectedBody!!.bodyVerify
        val actualBody = String(httpResponse.body)

        if (expectedBody == null && expectedBody != actualBody) {
            assertFailMatchingBody(expectedBody, actualBody, compareMode, getContextInfoForLogging(httpRequest, httpResponse))
        }

        val compareResult: JsonCompareResult = jsonComparer.compare(expectedBody!!, actualBody)
        if (compareResult is DifferentJsonCompareResult) {
            LOGGER.error("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")

            throw AssertionError(
                    "Expected Response Body to match [$expectedBody] but [$actualBody] found. \n" +
                            "\t Matching message: [${compareResult.message}] \n" +
                            "\t Not matching element path: [${compareResult.jsonPath}] \n" +
                            "\t Comparison Mode: [$compareMode] \n" +
                            getContextInfoForLogging(httpRequest, httpResponse)
            )
        }
    }

    private fun verifyBodyAsText(httpResponseVerify: HttpResponseVerify, httpRequest: HttpRequest, httpResponse: HttpResponse) {
        val compareMode = httpResponseVerify.expectedBody?.httpBodyVerifyMatchingType ?: EXACT_MATCH

        val expectedBody = httpResponseVerify.expectedBody!!.bodyVerify
        val actualBody = String(httpResponse.body)

        if (compareMode == EXACT_MATCH && expectedBody != actualBody) {
            assertFailMatchingBody(
                    expectedBody,
                    actualBody,
                    compareMode,
                    getContextInfoForLogging(httpRequest, httpResponse)
            )
        }

        if (compareMode == HttpBodyVerifyMatchingType.CONTAINS
                && ((expectedBody == null) || !actualBody.contains(expectedBody))) {
            assertFailMatchingBody(
                    expectedBody,
                    actualBody,
                    compareMode,
                    getContextInfoForLogging(httpRequest, httpResponse)
            )
        }

        if (compareMode == HttpBodyVerifyMatchingType.REGEX_MATCH
                && ((expectedBody == null) || !actualBody.contains(Regex(expectedBody)))) {
            assertFailMatchingBody(
                    expectedBody,
                    actualBody,
                    compareMode,
                    getContextInfoForLogging(httpRequest, httpResponse)
            )
        }

        LOGGER.debug(
                "Response Body Match \n" +
                        getContextInfoForLogging(httpRequest, httpResponse)
        )
    }

    private fun assertFailMatchingBody(
            expectedBody: String?,
            actualBody: String?,
            compareMode: HttpBodyVerifyMatchingType,
            contextInfo: String) {

        throw AssertionError(
                "Expected Response Body [$expectedBody] but [$actualBody] found. \n" +
                        "\tComparison Mode: [$compareMode]\n" +
                        contextInfo
        )
    }

    private fun verifyBodyIsEmpty(httpRequest: HttpRequest, httpResponse: HttpResponse) {
        val actualBody = httpResponse.body
        if (actualBody.isNotEmpty()) {
            throw AssertionError(
                    "Expected Http Response Body to be empty \n" +
                            getContextInfoForLogging(httpRequest, httpResponse)
            )
        } else {
            LOGGER.debug(
                    "Response Body is valid: [$IS_EMPTY] as expected"
            )
        }
    }

    private fun verifyExpectedHeaders(httpResponseVerify: HttpResponseVerify, httpResponse: HttpResponse, httpRequest: HttpRequest) {
        if (httpResponseVerify.expectedHeaders != null) {
            for (expectedHeader in httpResponseVerify.expectedHeaders!!) {
                val expectedHeaderKey = expectedHeader.key
                        ?: continue

                val compareMode = getCompareMode(expectedHeader)
                val actualHeader = getActualHeader(httpResponse, expectedHeaderKey)

                val contextInfo = getContextInfoForLogging(httpRequest, httpResponse)

                if (actualHeader == null) {
                    throw AssertionError(
                            "Expected Response Header: [${expectedHeader.key}] but no header with this key was found in the response: \n" +
                                    contextInfo
                    )
                }

                val expectedValue = expectedHeader.value
                val actualValues = actualHeader.values
                if (expectedValue == null && actualValues.isNotEmpty()) {
                    assertFailMatchingHeaders(
                            expectedHeader,
                            expectedValue,
                            actualValues[0],
                            HttpResponseVerifyHeadersCompareMode.EXACT_MATCH,
                            contextInfo
                    )
                }

                when (compareMode) {
                    HttpResponseVerifyHeadersCompareMode.EXACT_MATCH -> compareHeaderExactMode(expectedHeader, actualHeader, contextInfo)
                    CONTAINS -> compareHeaderContainsMode(expectedHeader, actualHeader, contextInfo)
                    HttpResponseVerifyHeadersCompareMode.REGEX_MATCH -> compareHeaderRegexMode(expectedHeader, actualHeader, contextInfo)
                }
            }
        }
    }

    private fun verifyExpectedCode(httpResponseVerify: HttpResponseVerify, httpResponse: HttpResponse, httpRequest: HttpRequest) {
        if (httpResponseVerify.expectedStatusCode != null) {
            if (httpResponseVerify.expectedStatusCode != httpResponse.statusCode) {
                throw AssertionError(
                        "Expected Status Code [${httpResponseVerify.expectedStatusCode}] but [${httpResponse.statusCode}] found \n" +
                                getContextInfoForLogging(httpRequest, httpResponse)
                )
            } else {
                "Status Code Found: expectedStatusCode = [${httpResponseVerify.expectedStatusCode}]"
            }
        }
    }

    private fun compareHeaderExactMode(expectedHeader: HttpResponseHeaderVerify, actualHeader: HttpResponseHeader, contextInfo: String) {

        val expectedValue = expectedHeader.value
        val actualValues = actualHeader.values

        for (actualValue in actualValues) {
            if (expectedValue == actualValue) {
                logHeaderFound(expectedHeader, actualValue, HttpResponseVerifyHeadersCompareMode.EXACT_MATCH)
                return
            }
        }

        assertFailMatchingHeaders(
                expectedHeader,
                expectedValue,
                actualValues[0],
                HttpResponseVerifyHeadersCompareMode.EXACT_MATCH,
                contextInfo
        )
    }

    private fun compareHeaderContainsMode(expectedHeader: HttpResponseHeaderVerify, actualHeader: HttpResponseHeader, contextInfo: String) {
        val expectedValue = expectedHeader.value
        val actualValues = actualHeader.values

        for (actualValue in actualValues) {
            if (expectedValue!!.contains(actualValue)) {
                logHeaderFound(expectedHeader, actualValue, CONTAINS)

                return
            }
        }

        assertFailMatchingHeaders(
                expectedHeader,
                expectedValue,
                actualValues[0],
                HttpResponseVerifyHeadersCompareMode.EXACT_MATCH,
                contextInfo
        )
    }

    private fun compareHeaderRegexMode(expectedHeader: HttpResponseHeaderVerify, actualHeader: HttpResponseHeader, contextInfo: String) {
        val expectedValue = expectedHeader.value
        val actualValues = actualHeader.values

        for (actualValue in actualValues) {
            if (expectedValue!!.contains(Regex(actualValue))) {
                logHeaderFound(expectedHeader, actualValue, HttpResponseVerifyHeadersCompareMode.REGEX_MATCH)
                return
            }
        }

        assertFailMatchingHeaders(
                expectedHeader,
                expectedValue,
                actualValues[0],
                HttpResponseVerifyHeadersCompareMode.REGEX_MATCH,
                contextInfo
        )
    }

    private fun logHeaderFound(expectedHeader: HttpResponseHeaderVerify,
                               actualValue: String,
                               compareMode: HttpResponseVerifyHeadersCompareMode) {

        LOGGER.debug("Header Found: \n" +
                "\t key=[${expectedHeader.key}], \n" +
                "\t expectedValue=[${expectedHeader.value}], \n" +
                "\t comparisonMode=[$compareMode], \n" +
                "\t actualValue=[$actualValue]"
        )
    }

    private fun assertFailMatchingHeaders(
            expectedHeader: HttpResponseHeaderVerify,
            expectedValue: String?,
            actualValue: String?,
            compareMode: HttpResponseVerifyHeadersCompareMode,
            contextInfo: String
    ) {

        throw AssertionError(
                "Expected Response Header [${expectedHeader.key}] with value [$expectedValue] but [$actualValue] found. \n" +
                        "\tComparison Mode: $compareMode.\n" +
                        contextInfo
        )
    }

    private fun getContextInfoForLogging(httpRequest: HttpRequest, httpResponse: HttpResponse): String {
        var response =
                "\t Http Request: [\n" +
                        "\t \t ${httpRequest.method} ${httpRequest.url} HTTP/1.1 \n"

        for (header in httpRequest.headers) {
            response +=
                    "\t \t ${header.key}: ${header.value} \n"
        }
        if (httpRequest.body != null)
            response +=
                    "\n" +
                    "\t \t ${httpRequest.body!!.content} \n"
        response +=
                "\t ]\n" +
                "\n"


        response +=
                "\t Http Response: [\n" +
                "\t \t HTTP/1.1 ${httpResponse.statusCode} \n"

        for (header in httpResponse.headers) {
            response +=
                    "\t \t ${header.key}: ${header.values.joinToString(",")} \n"
        }

        response +=
                "\n"
        "\t \t ${String(httpResponse.body)} \n"
        response +=
                "\t ]"

        return response
    }

    private fun getActualHeader(httpResponse: HttpResponse, expectedHeaderKey: String): HttpResponseHeader? {

        val headers: List<HttpResponseHeader> = httpResponse.headers
        for (header in headers) {
            if (header.key.equals(expectedHeaderKey, true)) {
                return header
            }
        }

        return null
    }

    private fun getCompareMode(header: HttpResponseHeaderVerify): HttpResponseVerifyHeadersCompareMode {
        var compareMode = header.compareMode
        if (compareMode == null) {
            compareMode = HttpResponseVerifyHeadersCompareMode.EXACT_MATCH
        }

        return compareMode
    }
}