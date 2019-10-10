package http.request

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import com.testerum.common_httpclient.HttpClientService
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.ValidHttpResponse
import http.request.transformer.HttpRequestTransformer
import http_support.HttpStepsSettingsManager
import http_support.logging.prettyPrint
import http_support.module_di.HttpStepsModuleServiceLocator

class HttpRequestSteps {

    private val httpClientService: HttpClientService = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.httpClientService
    private val httpStepsSettingsManager: HttpStepsSettingsManager = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.httpStepsSettingsManager
    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()

    @When(
            value = "I execute the HTTP request <<httpRequest>>",
            description =
            "Makes an HTTP request, saving the response as a test variable with the name ``httpResponse``.\n" +
                    "The request is also available in the variable ``httpRequest``.\n" +
                    "\n" +
                    "### Response\n" +
                    "\n" +
                    "```\n" +
                    "protocol         : String,  // HTTP/1.1\n" +
                    "statusCode       : int,     // 200\n" +
                    "headers          : List<HttpResponseHeader>,\n" +
                    "body             : byte[],  // body, exactly as received\n" +
                    "bodyAsUtf8String : String,\n" +
                    "jsonBody         : Map<String, String | int | boolean | null | Map}> // httpResponse.jsonBody.person.name\n" +
                    "```\n" +
                    "\n" +
                    "### HttpResponseHeader\n" +
                    "\n" +
                    "```\n" +
                    "key    : String,      // Content-Type\n" +
                    "values : List<String> // we use a list to capture all values (HTTP allows a header to be specified multiple times)\n" +
                    "```\n" +
                    "\n" +
                    "### Request\n" +
                    "\n" +
                    "```\n" +
                    "method          : HttpRequestMethod,\n" +
                    "url             : String\n" +
                    "headers         : Map<String, String>,\n" +
                    "body            : HttpRequestBody | null,\n" +
                    "followRedirects : boolean\n" +
                    "```\n" +
                    "\n" +
                    "### HttpRequestMethod\n" +
                    "\n" +
                    "```\n" +
                    "enum {\n" +
                    "    GET,\n" +
                    "    POST,\n" +
                    "    PUT,\n" +
                    "    DELETE,\n" +
                    "    HEAD,\n" +
                    "    OPTIONS,\n" +
                    "    TRACE,\n" +
                    "    PATCH\n" +
                    "}\n" +
                    "```\n" +
                    "\n" +
                    "### HttpRequestBody\n" +
                    "\n" +
                    "```\n" +
                    "bodyType : HttpRequestBodyType\n" +
                    "content  : String\n" +
                    "```\n" +
                    "\n" +
                    "### HttpRequestBodyType\n" +
                    "\n" +
                    "```\n" +
                    "enum {\n" +
                    "    RAW,\n" +
                    "    FORM_DATA,\n" +
                    "    X-WWW-FORM-URLENCODED,\n" +
                    "    BINARY\n" +
                    "}\n" +
                    "```\n" +
                    "\n"
    )
    fun testConnectionDetails(
            @Param(
                    transformer = HttpRequestTransformer::class,
                    description = "Details of the HTTP request to execute."
            )
            httpRequest: HttpRequest
    ) {
        logger.info("HTTP Request\n${httpRequest.prettyPrint()}\n")
        val httpResponse: ValidHttpResponse = httpClientService.executeHttpRequest(
                request = httpRequest,
                connectionTimeoutMillis = httpStepsSettingsManager.getConnectionTimeoutMillis(),
                socketTimeoutMillis = httpStepsSettingsManager.getSocketTimeoutMillis()
        )
        logger.info("HTTP Response\n${httpResponse.prettyPrint()}\n")

        variables["httpRequest"] = httpRequest
        variables["httpResponse"] = httpResponse

        logger.info("Http Request executed successfully")
    }

}
