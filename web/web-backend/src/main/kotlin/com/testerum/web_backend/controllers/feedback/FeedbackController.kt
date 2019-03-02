package com.testerum.web_backend.controllers.feedback

import com.testerum.model.feedback.ErrorFeedback
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/feedback")
open class FeedbackController() {

    @RequestMapping (method = [RequestMethod.POST],  path = ["error"])
    @ResponseBody
    fun createProject(@RequestBody errorFeedback: ErrorFeedback): ErrorFeedback {
        //TODO Cristi: implement this
        println("errorFeedback = ${errorFeedback}")
        return errorFeedback
    }
}
