package com.testerum.web_backend.services.resources.http

import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_kotlin.rootCause
import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.request.HttpRequestBody
import com.testerum.model.resources.http.response.HttpResponse
import com.testerum.model.resources.http.response.InvalidHttpResponse
import com.testerum.model.variable.ReservedVariableEnvironmentNames
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.services.variables.VariablesResolverService
import org.slf4j.LoggerFactory

class HttpFrontendService(private val webProjectManager: WebProjectManager,
                          private val httpClientService: HttpClientService,
                          private val variablesFileService: VariablesFileService,
                          private val variablesResolverService: VariablesResolverService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(HttpFrontendService::class.java)
    }

    private fun getProjectVariablesDir() = webProjectManager.getProjectServices().dirs().getVariablesDir()

    fun executeHttpRequest(request: HttpRequest): HttpResponse {
        return try {
            val resolvedRequest = resolveVariables(request)

            httpClientService.executeHttpRequest(resolvedRequest)
        } catch (e: Exception) {
            LOG.warn("could not execute HTTP request $request", e)

            InvalidHttpResponse(e.rootCause.message ?: "Unknown error while trying to get a response")
        }
    }

    private fun resolveVariables(request: HttpRequest): HttpRequest {
        val variablesMap = variablesFileService.getMergedVariables(
                projectVariablesDir = getProjectVariablesDir(),
                currentEnvironment = ReservedVariableEnvironmentNames.DEFAULT, // todo: pass from UI
                variableOverrides = emptyMap()
        )

        return resolveVariablesInRequest(request, variablesMap)
    }

    private fun resolveVariablesInRequest(request: HttpRequest,
                                          variablesMap: Map<String, String>): HttpRequest {
        val resolvedUrl = variablesResolverService.resolve(request.url, variablesMap)
        val resolvedHeaders = getResolvedVariablesInHeader(request.headers, variablesMap)
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

        return resultMap
    }

    private fun resolveVariablesInBody(body: HttpRequestBody,
                                         variablesMap: Map<String, String>): HttpRequestBody {
        return body.copy(
                content = variablesResolverService.resolve(body.content, variablesMap)
        )
    }
}
