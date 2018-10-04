package com.testerum.web_backend.services.resources.http

import com.testerum.common_httpclient.HttpClientService
import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.request.HttpRequestBody
import com.testerum.model.resources.http.response.HttpResponse
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.variables.VariablesResolverService

class HttpFrontendService(private val httpClientService: HttpClientService,
                          private val frontendDirs: FrontendDirs,
                          private val variablesFileService: VariablesFileService,
                          private val variablesResolverService: VariablesResolverService) {

    fun executeHttpRequest(request: HttpRequest): HttpResponse {
        val resolvedRequest = resolveVariables(request)

        return httpClientService.executeHttpRequest(resolvedRequest)
    }

    private fun resolveVariables(request: HttpRequest): HttpRequest {
        val variablesDir = frontendDirs.getRequiredVariablesDir()
        val variablesMap = variablesFileService.getVariablesAsMap(variablesDir)

        return resolveVariablesInRequest(request, variablesMap)
    }

    private fun resolveVariablesInRequest(request: HttpRequest,
                                          variablesMap: Map<String, String>): HttpRequest {
        val resolvedUrl = variablesResolverService.resolve(request.url, variablesMap)
        val resolvedHeaders = getResolvedVariablesInHeader(request.headers, variablesMap);
        val resolvedBody = request.body?.let {
            resolveVariablesInBody(it, variablesMap)
        }

        return request.copy(
                url = resolvedUrl,
                headers = resolvedHeaders,
                body = resolvedBody
        )
    }

    private fun getResolvedVariablesInHeader(headers: Map<String, String>,
                                         variablesMap: Map<String, String>): Map<String, String> {
        val resultMap = mutableMapOf<String, String>()
        for (header in headers) {
            resultMap.put(
                    key = variablesResolverService.resolve(header.key, variablesMap),
                    value = variablesResolverService.resolve(header.value, variablesMap)
            )
        }

        return resultMap;
    }

    private fun resolveVariablesInBody(body: HttpRequestBody,
                                         variablesMap: Map<String, String>): HttpRequestBody {
        return body.copy(
                content = variablesResolverService.resolve(body.content, variablesMap)
        )
    }
}
