package com.testerum.web_backend.services.feedback

import com.testerum.cloud_client.error_feedback.FeedbackCloudClient
import com.testerum.model.feedback.ErrorFeedback
import com.testerum.model.feedback.Feedback

class FeedbackFrontendService(private val feedbackCloudClient: FeedbackCloudClient) {

    fun sendErrorFeedback(errorFeedback: ErrorFeedback): ErrorFeedback {
        return feedbackCloudClient.sendErrorFeedback(errorFeedback)
    }

    fun sendFeedback(feedback: Feedback): Feedback {
        return feedbackCloudClient.sendFeedback(feedback)
    }
}