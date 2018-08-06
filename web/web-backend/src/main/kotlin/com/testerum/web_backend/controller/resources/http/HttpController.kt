package com.testerum.web_backend.controller.resources.http

import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.HttpResponse
import com.testerum.service.resources.http.HttpClientService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/http")
class HttpController(private val httpClientService: HttpClientService) {

    @RequestMapping(method = [RequestMethod.POST], path = ["/execute"])
    @ResponseBody
    fun schemas(@RequestBody httpRequest: HttpRequest): HttpResponse {
        return httpClientService.executeHttpRequest(httpRequest)
    }
}