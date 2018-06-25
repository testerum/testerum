package http.request

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.test_context.logger.TesterumLogger
import http.request.transformer.HttpRequestTransformer
import net.qutester.model.resources.http.request.HttpRequest
import net.qutester.model.resources.http.response.HttpResponse
import net.qutester.service.resources.http.HttpClientService
import net.testerum.resource_manager.TestContext
import org.springframework.beans.factory.annotation.Autowired

class HttpRequestSteps @Autowired constructor(val httpClientService: HttpClientService,
                                              val testContext: TestContext,
                                              val logger: TesterumLogger) {

    @When("I execute <<httpRequest>> HTTP Request")
    fun testConnectionDetails(@Param(transformer = HttpRequestTransformer::class) httpRequest: HttpRequest) {

        logger.logInfo("HTTP Request [\n$httpRequest\n]")
        val httpResponse: HttpResponse = httpClientService.executeHttpRequest(
                httpRequest
        )
        logger.logInfo("HTTP Response [\n$httpResponse\n]")

        testContext.addScenarioVariable("httpRequest", httpRequest)
        testContext.addScenarioVariable("httpResponse", httpResponse)

        logger.logInfo("Http Request executed successfully")
    }
}