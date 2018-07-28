package net.qutester.service.message

import net.qutester.model.message.Message
import net.qutester.model.message.MessageKey

class MessageService {

    fun getMessages(): List<Message> {
        val result = mutableListOf<Message>()
        for (messageKey in MessageKey.values()) {
            result.add(
                    Message(messageKey, messageKey.defaultValue)
            )
        }
        return result;
    }
}