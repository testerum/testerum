package http.request

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common_httpclient.HttpClientService
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.HttpResponse
import http.request.transformer.HttpRequestTransformer
import http_support.module_di.HttpStepsModuleServiceLocator

class HttpRequestSteps {

    private val httpClientService: HttpClientService = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.httpClientService
    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()

    @When("I execute <<httpRequest>> HTTP Request")
    fun testConnectionDetails(@Param(transformer = HttpRequestTransformer::class) httpRequest: HttpRequest) {
        logger.logInfo("HTTP Request [\n$httpRequest\n]")
        val httpResponse: HttpResponse = httpClientService.executeHttpRequest(httpRequest)
        logger.logInfo("HTTP Response [\n$httpResponse\n]")

        variables["httpRequest"] = httpRequest
        variables["httpResponse"] = httpResponse

        logger.logInfo("Http Request executed successfully")
    }

}