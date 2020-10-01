package com.testerum.report_generators.reports.remote_server

import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_httpclient.TesterumHttpClientFactory
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.request.HttpRequestBody
import com.testerum.model.resources.http.request.enums.HttpRequestBodyType
import com.testerum.model.resources.http.request.enums.HttpRequestMethod
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties.RemoteServer.REPORT_SERVER_URL
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent

class RemoteServerExecutionListener(private val properties: Map<String, String>) : ExecutionListener {


    private val reportServerUrl = properties[REPORT_SERVER_URL]
        ?: throw RuntimeException("${RunnerReportType.REMOTE_SERVER.toString()} requires the parameter \"${REPORT_SERVER_URL}\"")

    private val REPORTS_SERVER_BASE_URL = "$reportServerUrl/v1/reports"


    private var isPingSuccessful = false;
    private val eventsAsStringBuilder = StringBuilder()

    override fun start() {
        TesterumHttpClientFactory.createHttpClient().use { httpClient ->
            val httpClientService = HttpClientService(httpClient)

            val request = HttpRequest(
                method = HttpRequestMethod.GET,
                url = "$REPORTS_SERVER_BASE_URL/ping",
                body = null,
                followRedirects = true
            )
            try {
                val response = httpClientService.executeHttpRequest(request)

                if (response.statusCode != 200) {
                    throw RuntimeException(
                        """There is a problem with the Reports Server at URL: $reportServerUrl
                      |PING request details: $request
                      |PING response details: $response
                    """.trimMargin()
                    )
                }



                isPingSuccessful = true
            } catch (e: Exception) {
                throw RuntimeException(
                    """Can not connect to Reports Server at URL: $reportServerUrl
                      |PING request details: $request
                    """.trimMargin(),
                    e
                )
            }
        }
    }

    override fun onEvent(event: RunnerEvent) {
        if (eventsAsStringBuilder.isNotEmpty()) {
            eventsAsStringBuilder.appendln()
        }

        eventsAsStringBuilder.append(EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(event))
    }


    override fun stop() {
        if(!isPingSuccessful) return

        TesterumHttpClientFactory.createHttpClient().use { httpClient ->
            val httpClientService = HttpClientService(httpClient)

            val request = HttpRequest(
                method = HttpRequestMethod.POST,
                url = REPORTS_SERVER_BASE_URL,
                body = HttpRequestBody(HttpRequestBodyType.RAW, eventsAsStringBuilder.toString()),
                followRedirects = true
            )
            val response = httpClientService.executeHttpRequest(request)

            if (400 <= response.statusCode) {
                throw RuntimeException(
                    """An error occurred while calling the Report Server at URL: ${reportServerUrl}
                   HTTP Request: $request
                   HTTP Response: $response"""
                )
            }
        }
    }
}
