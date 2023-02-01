package com.testerum.cloud_client.error_feedback

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_kotlin.emptyToNull
import com.testerum.model.feedback.ErrorFeedback
import com.testerum.model.feedback.Feedback
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.util.ArrayList

class FeedbackCloudClient(private val httpClient: HttpClient,
                          private val baseUrl: String,
                          private val objectMapper: ObjectMapper) {


    companion object {
        private val LOG = LoggerFactory.getLogger(FeedbackCloudClient::class.java)
    }

    fun sendErrorFeedback(errorFeedback: ErrorFeedback): ErrorFeedback {
        val httpPost = HttpPost("$baseUrl/feedback_error")

        val bodyToSendJson = errorFeedback.copy(
                contactEmail = errorFeedback.contactEmail?.emptyToNull() ?: "email-not-provided@testerum.com"
        )

        val requestBodyAsString = objectMapper.writeValueAsString(bodyToSendJson)
        val requestEntity = StringEntity(
                requestBodyAsString,
                ContentType.APPLICATION_JSON)

        httpPost.entity = requestEntity

        return httpClient.execute(httpPost) { response ->
            val statusCode = response.statusLine.statusCode
            val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

            handleErrorFeedbackError(requestBodyAsString, statusCode, bodyAsString)

            errorFeedback;
        }
    }

    private fun handleErrorFeedbackError(requestBodyAsString: String, statusCode: Int, bodyAsString: String?) {
        if (statusCode > 400) {
            LOG.error("Error while sending Error Feedback to cloud. " +
                    "\n\t RequestBodyAsString: [$requestBodyAsString] " +
                    "\n\t ResponseStatusCode: [$statusCode] " +
                    "\n\t ResponseBodyAsString: [$bodyAsString]")
        }
    }

    fun sendFeedback(feedback: Feedback): Feedback {
        val httpPost = HttpPost("$baseUrl/contact")

        val requestBodyAsString = objectMapper.writeValueAsString(feedback)

        val postParameters = ArrayList<NameValuePair>()
        postParameters.add(BasicNameValuePair("g-recaptcha-response", "TesterumApp-MIIJKAIBAAKCA"))
        postParameters.add(BasicNameValuePair("name", feedback.name))
        postParameters.add(BasicNameValuePair("email", feedback.email?.emptyToNull() ?: "email-not-provided@testerum.com"))
        postParameters.add(BasicNameValuePair("message", feedback.message))

        httpPost.entity = UrlEncodedFormEntity(postParameters, "UTF-8")

        return httpClient.execute(httpPost) { response ->
            val statusCode = response.statusLine.statusCode
            val bodyAsString = EntityUtils.toString(response.entity, Charsets.UTF_8)

            handleFeedbackError(requestBodyAsString, statusCode, bodyAsString)

            feedback;
        }
    }

    private fun handleFeedbackError(requestBodyAsString: String, statusCode: Int, bodyAsString: String?) {
        if (statusCode > 400) {
            LOG.error("Error while sending Feedback to cloud. " +
                    "\n\t RequestBodyAsString: [$requestBodyAsString] " +
                    "\n\t ResponseStatusCode: [$statusCode] " +
                    "\n\t ResponseBodyAsString: [$bodyAsString]")
        }
    }
}
