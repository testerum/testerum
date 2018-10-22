package com.testerum.licenses.cloud_client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.licenses.cloud_client.create_trial_account.CreateTrialAccountCloudRequest
import com.testerum.licenses.cloud_client.create_trial_account.CreateTrialAccountCloudResponse
import com.testerum.licenses.cloud_client.login.LoginCloudRequest
import com.testerum.licenses.cloud_client.login.LoginCloudResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

class CloudClient(private val httpClient: HttpClient,
                  private val baseUrl: String,
                  private val objectMapper: ObjectMapper) {

    fun createTrialAccount(request: CreateTrialAccountCloudRequest): CreateTrialAccountCloudResponse {
        val httpPost = HttpPost("$baseUrl/create_trial_account")

        httpPost.entity = UrlEncodedFormEntity(
                listOf(
                        BasicNameValuePair("email", request.email),
                        BasicNameValuePair("password", request.password)
                )
        )

        return httpClient.execute(httpPost) { response ->
            val statusCode = response.statusLine.statusCode
            val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

            handleError(statusCode, bodyAsString)

            CreateTrialAccountCloudResponse(bodyAsString)
        }
    }

    fun login(request: LoginCloudRequest): LoginCloudResponse {
        val httpPost = HttpPost("$baseUrl/login")

        httpPost.entity = UrlEncodedFormEntity(
                listOf(
                        BasicNameValuePair("email", request.email),
                        BasicNameValuePair("password", request.password)
                )
        )

        return httpClient.execute(httpPost) { response ->
            val statusCode = response.statusLine.statusCode
            val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

            handleError(statusCode, bodyAsString)

            objectMapper.readValue(bodyAsString)
        }
    }

    private fun handleError(statusCode: Int, bodyAsString: String) {
        if (statusCode != HttpStatus.SC_OK) {
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

}