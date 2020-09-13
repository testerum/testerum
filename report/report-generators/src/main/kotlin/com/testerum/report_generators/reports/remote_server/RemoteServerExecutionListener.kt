package com.testerum.report_generators.reports.remote_server

import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_httpclient.TesterumHttpClientFactory
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.request.HttpRequestBody
import com.testerum.model.resources.http.request.enums.HttpRequestBodyType
import com.testerum.model.resources.http.request.enums.HttpRequestMethod
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties.JsonEvents.REPORT_SERVER_URL
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import java.lang.RuntimeException
import java.lang.StringBuilder

class RemoteServerExecutionListener(private val properties: Map<String, String>) : ExecutionListener {

    private val eventsAsStringBuilder = StringBuilder()

//TODO Ionut: is this the proper way to deal with a null param? Check with Cristi the error message
    private val reportServerUrl = properties[REPORT_SERVER_URL]
        ?: throw RuntimeException("${RunnerReportType.REMOTE_SERVER.toString()} requires the parameter \"${REPORT_SERVER_URL}\"")

    override fun start() {}

    override fun onEvent(event: RunnerEvent) {
        eventsAsStringBuilder.appendln(EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(event))
    }

    override fun stop() {

//TODO Ionut: this is a closable resource, see how you can close it properly
        val httpClient = TesterumHttpClientFactory.createHttpClient()
        val httpClientService = HttpClientService(httpClient)

        val request = HttpRequest(
            method = HttpRequestMethod.POST,
            url = "$reportServerUrl/report/v1",
            body = HttpRequestBody(HttpRequestBodyType.RAW, eventsAsStringBuilder.toString()),
            followRedirects = true
        )
        val response = httpClientService.executeHttpRequest(request)

        if (400 <= response.statusCode) {
            throw RuntimeException(
                """Exception appeared while calling the Report Server at URL: ${reportServerUrl}
                   HTTP Request: ${request}
                   HTTP Response: ${response}"""
            )
        }
    }
}
