package com.testerum.cloud_client.error_feedback

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.model.feedback.ErrorFeedback
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

class ErrorFeedbackCloudClient(private val httpClient: HttpClient,
                               private val baseUrl: String,
                               private val objectMapper: ObjectMapper) {


    companion object {
        private val LOG = LoggerFactory.getLogger(ErrorFeedbackCloudClient::class.java)
    }

    fun sendErrorFeedback(errorFeedback: ErrorFeedback): ErrorFeedback {
        val httpPost = HttpPost("$baseUrl/feedback_error")

        val requestBodyAsString = objectMapper.writeValueAsString(errorFeedback)
        val requestEntity = StringEntity(
                requestBodyAsString,
                ContentType.APPLICATION_JSON)

        httpPost.entity = requestEntity

        return httpClient.execute(httpPost) { response ->
            val statusCode = response.statusLine.statusCode
            val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

            handleError(requestBodyAsString, statusCode, bodyAsString)

            errorFeedback;
        }
    }

    private fun handleError(requestBodyAsString: String, statusCode: Int, bodyAsString: String?) {
        LOG.error("Error while sending Error Feedback to cloud. " +
                "\n\t RequestBodyAsString: [$requestBodyAsString] " +
                "\n\t ResponseStatusCode: [$statusCode] " +
                "\n\t ResponseBodyAsString: [$bodyAsString]")
    }
}
