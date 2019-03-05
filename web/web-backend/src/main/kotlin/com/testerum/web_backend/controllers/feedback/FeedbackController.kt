package com.testerum.web_backend.controllers.feedback

import com.testerum.model.feedback.ErrorFeedback
import com.testerum.web_backend.services.feedback.FeedbackFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/feedback")
open class FeedbackController(private val feedbackFrontendService: FeedbackFrontendService) {

    @RequestMapping (method = [RequestMethod.POST],  path = ["error"])
    @ResponseBody
    fun saveErrorFeedback(@RequestBody errorFeedback: ErrorFeedback): ErrorFeedback {
        return feedbackFrontendService.sendErrorFeedback(errorFeedback)
    }
}
