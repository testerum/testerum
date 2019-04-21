package com.testerum.cloud_client.licenses

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.cloud_client.CloudOfflineException
import com.testerum.cloud_client.infrastructure.CloudClientErrorResponseException
import com.testerum.cloud_client.infrastructure.CloudError
import com.testerum.cloud_client.infrastructure.ErrorCloudResponse
import com.testerum.cloud_client.licenses.model.auth.CloudAuthRequest
import com.testerum.cloud_client.licenses.model.auth.CloudAuthResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.net.ConnectException
import java.net.UnknownHostException

class LicenseCloudClient(private val httpClient: HttpClient,
                         private val baseUrl: String,
                         private val objectMapper: ObjectMapper) {

    companion object {
        private val OBJECT_MAPPER = ObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(KotlinModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun auth(request: CloudAuthRequest): String? {
        val url = "$baseUrl/web_auth"

        return handleOffline(url) {
            val httpPost = HttpPost(url)

            httpPost.entity = StringEntity(
                    OBJECT_MAPPER.writeValueAsString(request)
            )
            httpPost.addHeader("Content-Type", "application/json")

            httpClient.execute(httpPost) { response ->
                val statusCode = response.statusLine.statusCode
                val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    return@execute null
                }

                handleError(statusCode, bodyAsString)

                val cloudAuthResponse: CloudAuthResponse = objectMapper.readValue(bodyAsString)

                return@execute cloudAuthResponse.token
            }
        }
    }

    fun getSignedLicense(authToken: String): String {
        val url = "$baseUrl/get_license"

        return handleOffline(url) {
            val httpPost = HttpPost(url)

            httpPost.entity = UrlEncodedFormEntity(
                    listOf(
                            BasicNameValuePair("token", authToken)
                    )
            )

            httpClient.execute(httpPost) { response ->
                val statusCode = response.statusLine.statusCode
                val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

                handleError(statusCode, bodyAsString)

                bodyAsString
            }
        }
    }

    private fun <T> handleOffline(url: String, body: () -> T): T {
        try {
            return body()
        } catch (e : Exception) {
            when (e) {
                is ConnectException,
                is UnknownHostException -> throw CloudOfflineException("offline: could not reach [$url]", e)
                else -> throw e
            }
        }

    }

    private fun handleError(statusCode: Int, bodyAsString: String) {
        if (statusCode == HttpStatus.SC_OK) {
            return
        }

        val exceptionToThrow = try {
            val errorCloudResponse = objectMapper.readValue<ErrorCloudResponse>(bodyAsString)

            CloudClientErrorResponseException(errorCloudResponse)
        } catch (e: Exception) {
            CloudClientErrorResponseException(
                    ErrorCloudResponse(
                            CloudError(statusCode, bodyAsString)
                    )
            )
        }

        throw exceptionToThrow
    }

}
