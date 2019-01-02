package com.testerum.web_backend.controllers.runner.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.web_backend.services.runner.execution.TestsExecutionFrontendService
import com.testerum.web_backend.services.runner.result.ResultsFrontendService
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class TestsWebSocketController(private val testsExecutionFrontendService: TestsExecutionFrontendService,
                               private val objectMapper: ObjectMapper,
                               private val resultsFrontendService: ResultsFrontendService) : TextWebSocketHandler() {

    companion object {
        private val LOG = LoggerFactory.getLogger(TestsWebSocketController::class.java)

        private const val HANDLER_KEY_PAYLOAD_SEPARATOR = ":"
    }

    private val handlerMapping = mapOf<String /*handlerKey*/, (session: WebSocketSession, payload: String) -> Unit /*handler*/>(
            "EXECUTE-TESTS" to this::executeTests
    )

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val messagePayload: String = message.payload

        for ((handlerKey, handler) in handlerMapping) {
            if (messagePayload.startsWith(handlerKey + HANDLER_KEY_PAYLOAD_SEPARATOR)) {
                val payload: String = messagePayload.substring(handlerKey.length + HANDLER_KEY_PAYLOAD_SEPARATOR.length)

                try {
                    handler(session, payload)
                } catch (e: Exception) {
                    LOG.error("failed to handle WS message; handlerKey=[$handlerKey]", e)
                    throw e
                }
            }
        }
    }

    private fun executeTests(session: WebSocketSession,
                             payload: String) {
        val executionId: Long = payload.toLong()

        val resultFilePath = resultsFrontendService.createResultsDirectoryName()

        testsExecutionFrontendService.startExecution(
                executionId = executionId,
                reportsDestinationDirectory = resultFilePath,
                eventProcessor = { event ->
                    if (session.isOpen) {
                        // send to UI
                        val eventAsString = objectMapper.writeValueAsString(event)
                        session.sendMessageIgnoringErrors(TextMessage(eventAsString))
                    } else {
                        LOG.warn("webSocket communication is closed; will stop test execution")
                        testsExecutionFrontendService.stopExecution(executionId)
                    }
                },
                doneProcessor = {
                    session.closeIgnoringErrors()
                }
        )
    }

    private fun WebSocketSession.sendMessageIgnoringErrors(message: WebSocketMessage<*>) {
        try {
            sendMessage(message)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun WebSocketSession.closeIgnoringErrors() {
        try {
            close()
        } catch (e: Exception) {
            // ignore
        }
    }

}
