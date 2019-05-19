package com.testerum.cloud_client.licenses

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.cloud_client.CloudOfflineException
import com.testerum.cloud_client.infrastructure.CloudClientErrorResponseException
import com.testerum.cloud_client.infrastructure.CloudError
import com.testerum.cloud_client.infrastructure.ErrorCloudResponse
import com.testerum.cloud_client.licenses.model.auth.CloudAuthRequest
import com.testerum.cloud_client.licenses.model.auth.CloudAuthResponse
import com.testerum.cloud_client.licenses.model.get_updated_licenses.GetUpdatedLicensesRequestItem
import com.testerum.cloud_client.licenses.model.get_updated_licenses.GetUpdatedLicensesResponseItem
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

    fun auth(request: CloudAuthRequest): String? {
        val url = "$baseUrl/web_auth"

        return handleOffline(url) {
            val httpPost = HttpPost(url)

            httpPost.entity = StringEntity(
                    objectMapper.writeValueAsString(request)
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


                if (statusCode == HttpStatus.SC_BAD_REQUEST) {
                    // the authToken is invalid
                    throw CloudInvalidCredentialsException(bodyAsString)
                }

                if (statusCode == HttpStatus.SC_FORBIDDEN) {
                    // the user doesn't have any assigned license
                    throw CloudNoValidLicenseException(bodyAsString)
                }

                handleError(statusCode, bodyAsString)

                bodyAsString
            }
        }
    }

    fun getUpdatedLicenses(requestItems: List<GetUpdatedLicensesRequestItem>): List<GetUpdatedLicensesResponseItem> {
        val url = "$baseUrl/get_updated_licenses"

        return handleOffline(url) {
            val httpPost = HttpPost(url)

            httpPost.entity = StringEntity(
                    objectMapper.writeValueAsString(requestItems)
            )
            httpPost.addHeader("Content-Type", "application/json")

            httpClient.execute(httpPost) { response ->
                val statusCode = response.statusLine.statusCode
                val bodyAsString: String = EntityUtils.toString(response.entity, Charsets.UTF_8)

                handleError(statusCode, bodyAsString)

                val responseItems: List<GetUpdatedLicensesResponseItem> = objectMapper.readValue(bodyAsString)

                return@execute responseItems
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
