package http.mock

import com.testerum.api.annotations.hooks.BeforeEachTest
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.model.resources.http.mock.server.HttpMockServer
import com.testerum.model.resources.http.mock.stub.HttpMock
import http.mock.transformer.HttpMockServerTransformer
import http.mock.transformer.HttpMockTransformer
import http_support.HttpMockService
import http_support.module_di.HttpStepsModuleServiceLocator

class HttpMockSteps {

    private val httpMockService: HttpMockService = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.httpMockService

    @BeforeEachTest
    fun beforeTest() {
        httpMockService.clearAllStubs()
    }

    @Given(
            value = "the HTTP mock server <<httpMockServer>> with the mock request <<httpMock>>",
            description = "Sets up an HTTP server that will return the configured response when the configured request matches."
    )
    fun setupMockServer(
            @Param(
                    transformer = HttpMockServerTransformer::class,
                    description = "The configuration of the mock server, like the port on which to listen."
            )
            httpMockServer: HttpMockServer,

            @Param(
                    transformer = HttpMockTransformer::class,
                    description = "The expected request and the response to return for that request."
            )
            httpMock: HttpMock
    ) {
        httpMockService.addHttpStub(httpMockServer, httpMock)
    }
}
