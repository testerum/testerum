package com.testerum.web_backend.services.resources.http

import com.testerum.common_httpclient.HttpClientService
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.HttpResponse

class HttpFrontendService(private val httpClientService: HttpClientService) {

    fun executeHttpRequest(request: HttpRequest): HttpResponse = httpClientService.executeHttpRequest(request)

}
