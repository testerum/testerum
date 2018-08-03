package http_support

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.EqualJsonCompareResult
import net.qutester.model.resources.http.mock.server.HttpMockServer
import net.qutester.model.resources.http.mock.stub.HttpMock
import net.qutester.model.resources.http.mock.stub.enums.HttpMockFaultResponse
import net.qutester.model.resources.http.mock.stub.enums.HttpMockRequestBodyMatchingType
import net.qutester.model.resources.http.mock.stub.enums.HttpMockRequestHeadersCompareMode
import net.qutester.model.resources.http.mock.stub.enums.HttpMockRequestParamsCompareMode
import net.qutester.model.resources.http.mock.stub.request.HttpMockRequest
import javax.annotation.PreDestroy

class HttpMockService(val jsonComparer: JsonComparer) {

    private val servers: MutableMap<Int, WireMockServer> = mutableMapOf()

    @PreDestroy
    fun destroy() {
        servers.values.forEach{it.shutdownServer()}
    }

    fun addServerIfNotPresent(httpMockServer: HttpMockServer): WireMockServer {
        val port = httpMockServer.port
        
        if(!servers.containsKey(httpMockServer.port)) {
            val server: WireMockServer = WireMockServer(options().port(httpMockServer.port))
            server.start()
            servers.put(port,server)
        }

        return servers[port]!!
    }

    fun addHttpStub(httpMockServer: HttpMockServer, httpMock: HttpMock) {
        var server = servers[httpMockServer.port]
        if (server == null) {
            server = addServerIfNotPresent(httpMockServer)
        }

        val expectedRequest = httpMock.expectedRequest

        val mappingBuilder = request(expectedRequest.method.toString(), urlPathMatching(expectedRequest.url))

        addExpectedParams(mappingBuilder, expectedRequest)
        addExpectedHeaders(mappingBuilder, expectedRequest)
        addExpectedBody(mappingBuilder, expectedRequest)
        addExpectedScenario(mappingBuilder, expectedRequest)

        val responseBuilder = aResponse()

        addStub(responseBuilder, httpMock)
        addFaultResponse(responseBuilder, httpMock)
        addProxyResponse(responseBuilder, httpMock)

        mappingBuilder.willReturn(responseBuilder)

        server.stubFor(mappingBuilder)
    }

    private fun addProxyResponse(responseBuilder: ResponseDefinitionBuilder, httpMock: HttpMock) {
        if (httpMock.proxyResponse != null) {
            responseBuilder.proxiedFrom(httpMock.proxyResponse?.proxyBaseUrl)
        }
    }

    private fun addFaultResponse(responseBuilder: ResponseDefinitionBuilder, httpMock: HttpMock) {
        val faultResponse = httpMock.faultResponse
        if (faultResponse != null) {
            when (faultResponse) {
                HttpMockFaultResponse.NO_FAULT -> responseBuilder.withStatus(200)
                HttpMockFaultResponse.CLOSE_SOCKET_WITH_NO_RESPONSE -> responseBuilder.withFault(Fault.EMPTY_RESPONSE)
                HttpMockFaultResponse.SEND_BAD_HTTP_DATA_THEN_CLOSE_SOCKET -> responseBuilder.withFault(Fault.MALFORMED_RESPONSE_CHUNK)
                HttpMockFaultResponse.SEND_200_RESPONSE_THEN_BAD_HTTP_DATA_THEN_CLOSE_SOCKET -> responseBuilder.withFault(Fault.RANDOM_DATA_THEN_CLOSE)
                HttpMockFaultResponse.PEER_CONNECTION_RESET -> responseBuilder.withFault(Fault.CONNECTION_RESET_BY_PEER)
            }
        }
    }

    private fun addStub(responseBuilder: ResponseDefinitionBuilder, httpMock: HttpMock) {
        val mockResponse = httpMock.mockResponse
        if (mockResponse != null) {
            responseBuilder.withStatus(mockResponse.statusCode)

            mockResponse.headers?.forEach {
                responseBuilder.withHeader(it.key, it.value)
            }

            if (mockResponse.body != null) {
                responseBuilder.withBody(mockResponse.body?.content)
            }

            if (mockResponse.delay != null) {
                responseBuilder.withFixedDelay(
                        mockResponse.delay
                )
            }

        }
    }

    private fun addExpectedScenario(mappingBuilder: MappingBuilder, expectedRequest: HttpMockRequest) {
        if (expectedRequest.scenario != null) {
            val scenario = expectedRequest.scenario!!
            mappingBuilder.inScenario(scenario.scenarioName)
                    .whenScenarioStateIs(scenario.currentState)
                    .willSetStateTo(scenario.newState)
        }
    }

    private fun addExpectedBody(mappingBuilder: MappingBuilder, expectedRequest: HttpMockRequest) {
        if (expectedRequest.body != null) {
            val pattern: StringValuePattern?
            val body = expectedRequest.body!!
            when (body.matchingType) {
                HttpMockRequestBodyMatchingType.EXACT_MATCH -> pattern = equalTo(body.content)
                HttpMockRequestBodyMatchingType.CONTAINS -> pattern = containing(body.content)
                HttpMockRequestBodyMatchingType.REGEX_MATCH -> pattern = matching(body.content)
                HttpMockRequestBodyMatchingType.IS_EMPTY -> pattern = absent()
                HttpMockRequestBodyMatchingType.JSON_VERIFY -> pattern = object : StringValuePattern(body.content) {
                    override fun match(value: String?): MatchResult {
                        if (value == null) {
                            return MatchResult.noMatch()
                        }
                        val jsonCompareResult = jsonComparer.compare(expected, value)
                        if (jsonCompareResult is EqualJsonCompareResult) {
                            return MatchResult.exactMatch()
                        }
                        return MatchResult.noMatch()
                    }
                }
            }
            mappingBuilder.withRequestBody(pattern)
        }
    }

    private fun addExpectedHeaders(mappingBuilder: MappingBuilder, expectedRequest: HttpMockRequest) {
        expectedRequest.headers?.forEach {
            val pattern: StringValuePattern?
            when (it.compareMode) {
                HttpMockRequestHeadersCompareMode.EXACT_MATCH -> pattern = equalTo(it.value)
                HttpMockRequestHeadersCompareMode.CONTAINS -> pattern = containing(it.value)
                HttpMockRequestHeadersCompareMode.REGEX_MATCH -> pattern = matching(it.value)
                HttpMockRequestHeadersCompareMode.ABSENT -> pattern = absent()
                HttpMockRequestHeadersCompareMode.DOES_NOT_MATCH -> pattern = notMatching(it.value)
            }

            mappingBuilder.withHeader(it.key, pattern)
        }
    }

    private fun addExpectedParams(mappingBuilder: MappingBuilder, expectedRequest: HttpMockRequest) {
        expectedRequest.params?.forEach {
            val pattern: StringValuePattern?
            when (it.compareMode) {
                HttpMockRequestParamsCompareMode.EXACT_MATCH -> pattern = equalTo(it.value)
                HttpMockRequestParamsCompareMode.CONTAINS -> pattern = containing(it.value)
                HttpMockRequestParamsCompareMode.REGEX_MATCH -> pattern = matching(it.value)
                HttpMockRequestParamsCompareMode.ABSENT -> pattern = absent()
                HttpMockRequestParamsCompareMode.DOES_NOT_MATCH -> pattern = notMatching(it.value)
            }

            mappingBuilder.withQueryParam(it.key, pattern)
        }
    }

    fun clearAllStubs() {
        for (server in servers) {
            server.value.resetMappings()
        }
    }
}