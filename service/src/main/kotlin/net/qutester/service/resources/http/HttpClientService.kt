package net.qutester.service.resources.http

import com.google.common.collect.LinkedHashMultimap
import net.qutester.model.resources.http.request.HttpRequest
import net.qutester.model.resources.http.request.HttpRequestBody
import net.qutester.model.resources.http.request.enums.HttpRequestMethod
import net.qutester.model.resources.http.response.HttpResponse
import net.qutester.model.resources.http.response.HttpResponseHeader
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.*
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import java.net.URI


class HttpClientService(private val httpClient: HttpClient) {

    fun executeHttpRequest(request: HttpRequest): HttpResponse {
        // method
        val httpRequest: HttpRequestBase = when (request.method) {
            HttpRequestMethod.GET     -> HttpGet()
            HttpRequestMethod.POST    -> HttpPost()
            HttpRequestMethod.PUT     -> HttpPut()
            HttpRequestMethod.DELETE  -> HttpDelete()
            HttpRequestMethod.HEAD    -> HttpHead()
            HttpRequestMethod.OPTIONS -> HttpOptions()
            HttpRequestMethod.TRACE   -> HttpTrace()
            HttpRequestMethod.PATCH   -> HttpPatch()
            else                      -> throw RuntimeException("Unrecognized request method [${request.method}]")
        }

        // URI
        httpRequest.uri = URI.create(request.url)

        // body
        val body: HttpRequestBody? = request.body
        if (body != null) {
            if (httpRequest is HttpEntityEnclosingRequest) {
                val requestWithBody = httpRequest as HttpEntityEnclosingRequest

                val contentTypeHeaderValue: String? = request.getContentTypeHeaderValue()
                val contentType: ContentType? = if (contentTypeHeaderValue == null) {
                    null
                } else {
                    ContentType.parse(contentTypeHeaderValue)
                }

                requestWithBody.entity = StringEntity(body.content, contentType)
            } else {
                throw IllegalArgumentException("method [${request.method}] does not support a request body")
            }
        }

        // headers
        for (header in request.headers) {
            httpRequest.setHeader(header.key, header.value)
        }

        // call
        @Suppress("UnnecessaryVariable") // having a local variable helps debugging
        val response = httpClient.execute(httpRequest) {
            convertResponse(it)
        }

        return response
    }

    private fun convertResponse(httpResponse: org.apache.http.HttpResponse): HttpResponse {
        val headers = LinkedHashMultimap.create<String, String>()

        for (header in httpResponse.allHeaders) {
            headers.put(header.name, header.value)
        }

        val headersListModel = headers.asMap().map { entry ->
            HttpResponseHeader(
                    key = entry.key,
                    values = ArrayList(entry.value)
            )
        }

        return HttpResponse(
                protocol = httpResponse.protocolVersion.toString(),
                statusCode = httpResponse.statusLine.statusCode,
                headers = headersListModel,
                body = httpResponse.entity?.let { EntityUtils.toByteArray(it) } ?: ByteArray(0)
        )
    }

}