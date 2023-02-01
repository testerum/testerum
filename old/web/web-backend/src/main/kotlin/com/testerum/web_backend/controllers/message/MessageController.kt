package com.testerum.web_backend.controllers.message

import com.testerum.model.message.Message
import com.testerum.web_backend.services.message.MessageFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
class MessageController(val messageFrontendService: MessageFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getMessages(): List<Message> {
        return messageFrontendService.getMessages()
    }

}
