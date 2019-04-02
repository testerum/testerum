package com.testerum.web_backend.services.feedback

import com.testerum.cloud_client.error_feedback.ErrorFeedbackCloudClient
import com.testerum.model.feedback.ErrorFeedback

class FeedbackFrontendService(private val errorFeedbackCloudClient: ErrorFeedbackCloudClient) {

    fun sendErrorFeedback(errorFeedback: ErrorFeedback): ErrorFeedback {
        return errorFeedbackCloudClient.sendErrorFeedback(errorFeedback)
    }
}