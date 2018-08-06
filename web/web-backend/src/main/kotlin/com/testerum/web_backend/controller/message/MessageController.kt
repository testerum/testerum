package com.testerum.web_backend.controller.message

import com.testerum.model.message.Message
import com.testerum.service.message.MessageService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
open class MessageController(val messageService: MessageService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getSettings(): List<Message> {
        return messageService.getMessages()
    }
}