package com.testerum.web_backend.controller.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.model.test.TestModel
import com.testerum.service.tests_runner.execution.TestsExecutionService
import com.testerum.service.tests_runner.result.TestRunnerResultService
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TestsWebSocketController(private val testsExecutionService: TestsExecutionService,
                               private val objectMapper: ObjectMapper,
                               private val fileRepositoryService: FileRepositoryService,
                               private val testRunnerResultService: TestRunnerResultService) : TextWebSocketHandler() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TestsWebSocketController::class.java)

        private const val HANDLER_KEY_PAYLOAD_SEPARATOR = ":"
        private val FILE_NAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss_SSS")
    }

    private val handlerMapping = mapOf<String /*handlerKey*/, (session: WebSocketSession, payload: String) -> Unit /*handler*/>(
            "EXECUTE-TESTS" to this::executeTestsHandler
    )

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val messagePayload: String = message.payload

        for ((handlerKey, handler) in handlerMapping) {
            if (messagePayload.startsWith(handlerKey + HANDLER_KEY_PAYLOAD_SEPARATOR)) {
                val payload: String = messagePayload.substring(handlerKey.length + HANDLER_KEY_PAYLOAD_SEPARATOR.length)

                try {
                    handler(session, payload)
                } catch (e: Exception) {
                    LOGGER.error("failed to handle WS message; handlerKey=[$handlerKey]", e)
                    throw e
                }
            }
        }
    }

    private fun executeTestsHandler(session: WebSocketSession,
                                    payload: String) {
        val indexOfSeparator = payload.indexOf(HANDLER_KEY_PAYLOAD_SEPARATOR)

        val executionIdAsText: String = payload.substring(0, indexOfSeparator)
        val testModelsAsText: String = payload.substring(indexOfSeparator + 1)

        val executionId: Long = executionIdAsText.toLong()

        val testModels: List<TestModel> = objectMapper.readValue(testModelsAsText)
        val testPaths: List<Path> = testModels.map { it.path }

        val resultFilePath = createResultsFile()

        testsExecutionService.startExecution(executionId, testPaths) { event ->
            if (session.isOpen) {
                // send to UI
                val eventAsString = objectMapper.writeValueAsString(event)
                session.sendMessage(TextMessage(eventAsString))

                // save to file
                testRunnerResultService.saveEvent(event, resultFilePath)
            } else {
                LOGGER.warn("webSocket communication is closed; will stop test execution")
                testsExecutionService.stopExecution(executionId)
            }
        }
    }

    private fun createResultsFile(): Path {
        val localDate: LocalDateTime = LocalDateTime.now()
        val directoryName: String = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val fileName: String = localDate.format(FILE_NAME_FORMATTER)

        val resultFilePath = Path(listOf(directoryName), fileName, FileType.RESULT.fileExtension)
        fileRepositoryService.createFile(
                KnownPath(resultFilePath, FileType.RESULT)
        )

        return resultFilePath
    }

}
