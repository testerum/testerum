package com.testerum.common_httpclient

import com.testerum.model.resources.http.request.HttpRequest
import com.testerum.model.resources.http.request.HttpRequestBody
import com.testerum.model.resources.http.request.enums.HttpRequestMethod
import com.testerum.model.resources.http.response.HttpResponseHeader
import com.testerum.model.resources.http.response.ValidHttpResponse
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.methods.HttpTrace
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import java.net.URI
import java.util.concurrent.TimeUnit


class HttpClientService(private val httpClient: HttpClient) {

    /**
     * @param connectionTimeoutMillis
     *      The timeout in milliseconds until a connection is established.
     *      A timeout value of zero is interpreted as an infinite timeout.
     * @param socketTimeoutMillis
     *      The timeout in milliseconds for waiting for data, or, put differently
     *      the maximum period of inactivity between two consecutive data packets.
     *      A timeout value of zero is interpreted as an infinite timeout.
     */
    fun executeHttpRequest(request: HttpRequest,
                           connectionTimeoutMillis: Int = 0,
                           socketTimeoutMillis: Int = 0): ValidHttpResponse {
        // method
        val httpRequest: HttpRequestBase = when (request.method) {
            HttpRequestMethod.GET -> HttpGet()
            HttpRequestMethod.POST -> HttpPost()
            HttpRequestMethod.PUT -> HttpPut()
            HttpRequestMethod.DELETE -> HttpDelete()
            HttpRequestMethod.HEAD -> HttpHead()
            HttpRequestMethod.OPTIONS -> HttpOptions()
            HttpRequestMethod.TRACE -> HttpTrace()
            HttpRequestMethod.PATCH -> HttpPatch()
            else                      -> throw RuntimeException("Unrecognized request method [${request.method}]")
        }

        // request configuration
        val requestConfig = if (httpRequest.config == null) {
            RequestConfig.DEFAULT
        } else {
            httpRequest.config
        }
        httpRequest.config = RequestConfig.copy(requestConfig)
                .setRedirectsEnabled(request.followRedirects)
                .setConnectTimeout(connectionTimeoutMillis)
                .setSocketTimeout(socketTimeoutMillis)
                .build()

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
        val requestStartTime = System.nanoTime()
        val httpResponse = httpClient.execute(httpRequest)
        val responseDurationInMillis: Long = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartTime)

        return convertResponse(httpResponse, responseDurationInMillis)
    }

    private fun convertResponse(httpResponse: HttpResponse, responseDurationInMillis: Long): ValidHttpResponse {
        val headers = LinkedHashMap<String, MutableList<String>>()

        for (header in httpResponse.allHeaders) {
            headers.compute(header.name) { _, existingValue ->
                if (existingValue == null) {
                    ArrayList<String>().apply {
                        add(header.value)
                    }
                } else {
                    existingValue.add(header.value)

                    existingValue
                }
            }
        }

        val headersListModel = headers.map { entry ->
            HttpResponseHeader(
                key = entry.key,
                values = ArrayList(entry.value)
            )
        }

        return ValidHttpResponse(
            protocol = httpResponse.protocolVersion.toString(),
            statusCode = httpResponse.statusLine.statusCode,
            headers = headersListModel,
            body = httpResponse.entity?.let { EntityUtils.toByteArray(it) } ?: ByteArray(0),
            durationInMillis = responseDurationInMillis
        )
    }
}
