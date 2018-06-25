package http.mock

import com.testerum.api.annotations.hooks.BeforeEachTest
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import http.mock.transformer.HttpMockServerTransformer
import http.mock.transformer.HttpMockTransformer
import http_support.HttpMockService
import net.qutester.model.resources.http.mock.server.HttpMockServer
import net.qutester.model.resources.http.mock.stub.HttpMock
import org.springframework.beans.factory.annotation.Autowired

class HttpMockSteps(@Autowired val httpMockService: HttpMockService) {

    @BeforeEachTest
    fun beforeTest() {
        httpMockService.clearAllStubs();
    }

    @Given("The HTTP Mock Server <<httpMockServer>> with the Mock Request <<httpMock>>")
    fun testConnectionDetails(@Param(transformer= HttpMockServerTransformer::class) httpMockServer: HttpMockServer,
                              @Param(transformer= HttpMockTransformer::class) httpMock: HttpMock) {

        httpMockService.addHttpStub(httpMockServer, httpMock)
    }
}