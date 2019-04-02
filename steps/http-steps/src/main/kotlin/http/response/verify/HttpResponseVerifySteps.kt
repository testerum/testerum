package http.response.verify

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import com.testerum.common_json.util.prettyPrintJson
import com.testerum.model.resources.http.response.HttpResponseHeader
import com.testerum.model.resources.http.response.ValidHttpResponse
import http.response.verify.model.HttpBodyVerifyMatchingType
import http.response.verify.model.HttpBodyVerifyMatchingType.EXACT_MATCH
import http.response.verify.model.HttpBodyVerifyMatchingType.IS_EMPTY
import http.response.verify.model.HttpBodyVerifyMatchingType.JSON_VERIFY
import http.response.verify.model.HttpResponseHeaderVerify
import http.response.verify.model.HttpResponseVerify
import http.response.verify.model.HttpResponseVerifyHeadersCompareMode
import http.response.verify.model.HttpResponseVerifyHeadersCompareMode.CONTAINS
import http.response.verify.transformer.HttpResponseVerifyTransformer
import http_support.logging.prettyPrint
import http_support.logging.prettyPrintHttpStatusCode
import http_support.module_di.HttpStepsModuleServiceLocator

class HttpResponseVerifySteps {

    private val jsonComparer: JsonComparer = HttpStepsModuleServiceLocator.bootstrapper.jsonDiffModuleFactory.jsonComparer
    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()

    @Then(
            value = "the HTTP response should be <<httpResponseVerify>>",
            description = "Checks if the response from the previous HTTP request is according to the given criteria."
    )
    fun verifyHttpResponse(
            @Param(
                    transformer = HttpResponseVerifyTransformer::class,
                    description = "The criteria describing the expected HTTP response."
            )
            httpResponseVerify: HttpResponseVerify
    ) {
        val httpResponse: ValidHttpResponse = variables["httpResponse"] as ValidHttpResponse

        logger.info("Expected HTTP Response\n${httpResponseVerify.prettyPrint()}\n")
        logger.info("Actual HTTP Response\n${httpResponse.prettyPrint()}\n")

        verifyExpectedCode(httpResponseVerify, httpResponse)
        verifyExpectedHeaders(httpResponseVerify, httpResponse)
        verifyExpectedBody(httpResponseVerify, httpResponse)
    }

    private fun verifyExpectedCode(httpResponseVerify: HttpResponseVerify, httpResponse: ValidHttpResponse) {
        if (httpResponseVerify.expectedStatusCode != null) {
            if (httpResponseVerify.expectedStatusCode != httpResponse.statusCode) {
                throw AssertionError("Expected Status Code [${httpResponseVerify.expectedStatusCode?.prettyPrintHttpStatusCode()}] but [${httpResponse.statusCode.prettyPrintHttpStatusCode()}] found")
            }
        }
    }

    private fun verifyExpectedHeaders(httpResponseVerify: HttpResponseVerify, httpResponse: ValidHttpResponse) {
        if (httpResponseVerify.expectedHeaders != null) {
            for (expectedHeader in httpResponseVerify.expectedHeaders!!) {
                val expectedHeaderKey = expectedHeader.key
                        ?: continue

                val compareMode = getCompareMode(expectedHeader)
                val actualHeader = getActualHeader(httpResponse, expectedHeaderKey)
                        ?: throw AssertionError("Expected Response Header: [${expectedHeader.key}] but no header with this key was found in the response.")

                val expectedValue = expectedHeader.value
                val actualValues = actualHeader.values
                if (expectedValue == null && actualValues.isNotEmpty()) {
                    assertFailMatchingHeaders(
                            expectedHeader,
                            expectedValue,
                            actualValues[0],
                            HttpResponseVerifyHeadersCompareMode.EXACT_MATCH
                    )
                }

                when (compareMode) {
                    HttpResponseVerifyHeadersCompareMode.EXACT_MATCH -> compareHeaderExactMode(expectedHeader, actualHeader)
                    CONTAINS -> compareHeaderContainsMode(expectedHeader, actualHeader)
                    HttpResponseVerifyHeadersCompareMode.REGEX_MATCH -> compareHeaderRegexMode(expectedHeader, actualHeader)
                }
            }
        }
    }

    private fun verifyExpectedBody(httpResponseVerify: HttpResponseVerify, httpResponse: ValidHttpResponse) {
        val expectedBody = httpResponseVerify.expectedBody ?: return

        val matchingType: HttpBodyVerifyMatchingType = expectedBody.httpBodyVerifyMatchingType
        when (matchingType) {
            IS_EMPTY -> verifyBodyIsEmpty(httpResponse)
            JSON_VERIFY -> verifyBodyAsJsonVerify(httpResponseVerify, httpResponse)
            HttpBodyVerifyMatchingType.CONTAINS, HttpBodyVerifyMatchingType.REGEX_MATCH, EXACT_MATCH -> verifyBodyAsText(httpResponseVerify, httpResponse)
        }

    }

    private fun verifyBodyAsJsonVerify(httpResponseVerify: HttpResponseVerify, httpResponse: ValidHttpResponse) {
        val compareMode = httpResponseVerify.expectedBody?.httpBodyVerifyMatchingType ?: EXACT_MATCH

        val expectedBody = httpResponseVerify.expectedBody!!.bodyVerify
        val actualBody = String(httpResponse.body)

        if (expectedBody == null && expectedBody != actualBody) {
            assertFailMatchingBody(expectedBody, actualBody, compareMode)
        }

        val compareResult: JsonCompareResult = jsonComparer.compare(expectedBody!!, actualBody)
        if (compareResult is DifferentJsonCompareResult) {
            logger.error("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")

            val prettyExpectedBody = expectedBody.prettyPrintJson()
                    .lines()
                    .joinToString(separator = "\n") {
                        "\t\t$it"
                    }
            val prettyActualBody = actualBody.prettyPrintJson()
                    .lines()
                    .joinToString(separator = "\n") {
                        "\t\t$it"
                    }

            throw AssertionError(
                    "Expected Response Body to match\n" +
                            "$prettyExpectedBody\n" +
                            "\tbut found\n" +
                            "$prettyActualBody\n" +
                            "\tMatching message: [${compareResult.message}] \n" +
                            "\tNot matching element path: [${compareResult.jsonPath}] \n" +
                            "\tComparison Mode: [$compareMode] \n"
            )
        }
    }

    private fun verifyBodyAsText(httpResponseVerify: HttpResponseVerify, httpResponse: ValidHttpResponse) {
        val compareMode = httpResponseVerify.expectedBody?.httpBodyVerifyMatchingType ?: EXACT_MATCH

        val expectedBody = httpResponseVerify.expectedBody!!.bodyVerify
        val actualBody = String(httpResponse.body)

        if (compareMode == EXACT_MATCH && expectedBody != actualBody) {
            assertFailMatchingBody(
                    expectedBody,
                    actualBody,
                    compareMode
            )
        }

        if (compareMode == HttpBodyVerifyMatchingType.CONTAINS
                && ((expectedBody == null) || !actualBody.contains(expectedBody))) {
            assertFailMatchingBody(
                    expectedBody,
                    actualBody,
                    compareMode
            )
        }

        if (compareMode == HttpBodyVerifyMatchingType.REGEX_MATCH
                && ((expectedBody == null) || !actualBody.contains(Regex(expectedBody)))) {
            assertFailMatchingBody(
                    expectedBody,
                    actualBody,
                    compareMode
            )
        }
    }

    private fun assertFailMatchingBody(expectedBody: String?,
                                       actualBody: String?,
                                       compareMode: HttpBodyVerifyMatchingType) {

        val prettyExpectedBody = expectedBody.orEmpty()
                .lines()
                .joinToString(separator = "\n") {
                    "\t\t$it"
                }
        val prettyActualBody = actualBody.orEmpty()
                .lines()
                .joinToString(separator = "\n") {
                    "\t\t$it"
                }
        throw AssertionError(
                "Expected Response Body\n" +
                        "$prettyExpectedBody\n" +
                        "\tbut found\n" +
                        "$prettyActualBody\n" +
                        "\tComparison Mode: [$compareMode]\n"
        )
    }

    private fun verifyBodyIsEmpty(httpResponse: ValidHttpResponse) {
        val actualBody = httpResponse.body
        if (actualBody.isNotEmpty()) {
            throw AssertionError("Expected Http Response Body to be empty")
        } else {
            logger.debug(
                    "Response Body is valid: [$IS_EMPTY] as expected"
            )
        }
    }

    private fun compareHeaderExactMode(expectedHeader: HttpResponseHeaderVerify, actualHeader: HttpResponseHeader) {

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
                HttpResponseVerifyHeadersCompareMode.EXACT_MATCH
        )
    }

    private fun compareHeaderContainsMode(expectedHeader: HttpResponseHeaderVerify, actualHeader: HttpResponseHeader) {
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
                HttpResponseVerifyHeadersCompareMode.EXACT_MATCH
        )
    }

    private fun compareHeaderRegexMode(expectedHeader: HttpResponseHeaderVerify, actualHeader: HttpResponseHeader) {
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
                HttpResponseVerifyHeadersCompareMode.REGEX_MATCH
        )
    }

    private fun logHeaderFound(expectedHeader: HttpResponseHeaderVerify,
                               actualValue: String,
                               compareMode: HttpResponseVerifyHeadersCompareMode) {

        logger.debug("Header Found: \n" +
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
            compareMode: HttpResponseVerifyHeadersCompareMode
    ) {

        throw AssertionError(
                "Expected Response Header [${expectedHeader.key}] with value [$expectedValue] but [$actualValue] found" +
                        "; comparison mode: $compareMode.\n"
        )
    }

    private fun getActualHeader(httpResponse: ValidHttpResponse, expectedHeaderKey: String): HttpResponseHeader? {

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
