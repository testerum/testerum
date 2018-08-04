package net.qutester.controller.message

import net.qutester.model.message.Message
import net.qutester.service.message.MessageService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
open class MessageController(val messageService: MessageService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getSettings(): List<Message> {
        return messageService.getMessages()
    }
}