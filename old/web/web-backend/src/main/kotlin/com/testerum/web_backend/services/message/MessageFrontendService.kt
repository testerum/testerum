package com.testerum.web_backend.services.message

import com.testerum.model.message.Message
import com.testerum.model.message.MessageKey

class MessageFrontendService {

    fun getMessages(): List<Message> {
        return MessageKey.values()
                .asList()
                .map {
                    Message(it, it.defaultValue)
                }
    }

}
