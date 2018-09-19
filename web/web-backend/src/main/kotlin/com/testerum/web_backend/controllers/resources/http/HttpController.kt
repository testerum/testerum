package com.testerum.web_backend.controllers.resources.http

import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.response.HttpResponse
import com.testerum.web_backend.services.resources.http.HttpFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/http")
class HttpController(private val httpFrontendService: HttpFrontendService) {

    @RequestMapping(method = [RequestMethod.POST], path = ["/execute"])
    @ResponseBody
    fun executeHttpRequest(@RequestBody httpRequest: HttpRequest): HttpResponse {
        return httpFrontendService.executeHttpRequest(httpRequest)
    }

}
