package net.qutester.controller.resources.http

import net.qutester.model.resources.http.request.HttpRequest
import net.qutester.model.resources.http.response.HttpResponse
import net.qutester.service.resources.http.HttpClientService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/http")
class HttpController(private val httpClientService: HttpClientService) {

    @RequestMapping(path = ["/execute"], method = [RequestMethod.POST])
    @ResponseBody
    fun schemas(@RequestBody httpRequest: HttpRequest): HttpResponse {
        return httpClientService.executeHttpRequest(httpRequest)
    }
}