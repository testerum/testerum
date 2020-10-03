package com.testerum.report_generators.reports.remote_server

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_httpclient.TesterumHttpClientFactory
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.request.HttpRequestBody
import com.testerum.model.resources.http.request.enums.HttpRequestBodyType
import com.testerum.model.resources.http.request.enums.HttpRequestMethod
import com.testerum.model.resources.http.response.ValidHttpResponse
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties.RemoteServer.REPORT_SERVER_URL
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent

class RemoteServerExecutionListener(private val properties: Map<String, String>) : ExecutionListener {

    private val reportServerUrl = properties[REPORT_SERVER_URL]
        ?: throw IllegalArgumentException("${RunnerReportType.REMOTE_SERVER} requires the property \"$REPORT_SERVER_URL\"")

    private var isServerHealthy = false;
    private val eventsAsStringBuilder = StringBuilder()

    override fun start() {
        TesterumHttpClientFactory.createHttpClient().use { httpClient ->
            val httpClientService = HttpClientService(httpClient)

            val request = HttpRequest(
                method = HttpRequestMethod.GET,
                url = "$reportServerUrl/management/health",
                body = null,
                followRedirects = true
            )
            try {
                val response = httpClientService.executeHttpRequest(request)

                if (response.statusCode != 200 || !serverIsUp(response)) {
                    throw RuntimeException(
                        """|The Reports Server did not start correctly:
                           |HTTP Request details: $request
                           |HTTP Response details: $response""".trimMargin()
                    )
                }

                isServerHealthy = true
            } catch (e: Exception) {
                throw RuntimeException(
                    """|Can not connect to Reports Server at URL: $reportServerUrl
                       |HTTP Request details: $request""".trimMargin(),
                    e
                )
            }
        }
    }

    private fun serverIsUp(response: ValidHttpResponse): Boolean {
        return try {
            val healthResponse = ValidHttpResponse.OBJECT_MAPPER.readValue<HealthResponse>(response.body)

            healthResponse.status == "UP"
        } catch (e: Exception) {
            false
        }
    }

    override fun onEvent(event: RunnerEvent) {
        if (eventsAsStringBuilder.isNotEmpty()) {
            eventsAsStringBuilder.appendln()
        }

        eventsAsStringBuilder.append(EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(event))
    }

    override fun stop() {
        if (!isServerHealthy) {
            return
        }

        TesterumHttpClientFactory.createHttpClient().use { httpClient ->
            val httpClientService = HttpClientService(httpClient)

            val request = HttpRequest(
                method = HttpRequestMethod.POST,
                url = "$reportServerUrl/v1/reports",
                body = HttpRequestBody(HttpRequestBodyType.RAW, eventsAsStringBuilder.toString()),
                followRedirects = true
            )
            val response = httpClientService.executeHttpRequest(request)

            if (400 <= response.statusCode) {
                throw RuntimeException(
                    """|An error occurred while calling the Report Server at URL: ${reportServerUrl}
                       |HTTP Request: $request
                       |HTTP Response: $response""".trimMargin()
                )
            }
        }
    }

    private class HealthResponse(val status: String)
}
