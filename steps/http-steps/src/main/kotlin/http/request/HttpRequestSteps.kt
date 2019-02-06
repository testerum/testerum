package http.request

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_httpclient.util.MediaTypeUtils
import com.testerum.common_json.util.prettyPrintJson
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.ValidHttpResponse
import http.request.transformer.HttpRequestTransformer
import http_support.module_di.HttpStepsModuleServiceLocator
import org.apache.http.impl.EnglishReasonPhraseCatalog
import org.slf4j.LoggerFactory
import java.util.*

class HttpRequestSteps {

    companion object {
        private val LOG = LoggerFactory.getLogger(HttpRequestSteps::class.java)
    }

    private val httpClientService: HttpClientService = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.httpClientService
    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()

    @When(
            value = "I execute <<httpRequest>> HTTP Request",
            description = "Makes an HTTP request, saving the response as a test variable with the name ``httpResponse``.\n" +
                          "The request is also available in the variable ``httpRequest``"
    )
    fun testConnectionDetails(
            @Param(
                    transformer = HttpRequestTransformer::class,
                    description = "Details of the HTTP request to execute."
            )
            httpRequest: HttpRequest
    ) {
        logger.info("HTTP Request\n${httpRequest.prettyPrint()}\n")
        val httpResponse: ValidHttpResponse = httpClientService.executeHttpRequest(httpRequest)
        logger.info("HTTP Response\n${httpResponse.prettyPrint()}\n")

        variables["httpRequest"] = httpRequest
        variables["httpResponse"] = httpResponse

        logger.info("Http Request executed successfully")
    }

    private fun HttpRequest.prettyPrint(): String {
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
            response +=  "\n"
            response += formatHttpBody(
                    body = body.content,
                    contentType = getContentTypeHeaderValue().orEmpty(),
                    bodyDescription = "HTTP request body"
            )
        }

        return response
    }

    private fun ValidHttpResponse.prettyPrint(): String {
        var response = "\t$protocol $statusCode ${EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.ENGLISH)}\n"
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
            response +=  "\n"
            response += formatHttpBody(
                    body = bodyAsUtf8String,
                    contentType = contentType,
                    bodyDescription = "HTTP response body"
            )
        }

        return response
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
            body
        }
    }

}
