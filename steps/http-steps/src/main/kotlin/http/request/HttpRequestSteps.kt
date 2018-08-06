package http.request

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.HttpResponse
import http.request.transformer.HttpRequestTransformer
import net.qutester.service.resources.http.HttpClientService
import org.springframework.beans.factory.annotation.Autowired

@Suppress("MemberVisibilityCanBePrivate", "unused")
class HttpRequestSteps @Autowired constructor(val httpClientService: HttpClientService,
                                              val variables: TestVariables,
                                              val logger: TesterumLogger) {

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