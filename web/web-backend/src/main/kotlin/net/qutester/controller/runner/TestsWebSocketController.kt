package net.qutester.controller.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.runner.events.model.RunnerErrorEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import net.qutester.model.test.TestModel
import net.qutester.service.tests_runner.TestsRunnerService
import net.qutester.service.tests_runner.event_bus.TestRunnerEventBus
import net.qutester.service.tests_runner.event_bus.TestRunnerEventListener
import org.slf4j.LoggerFactory
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class TestsWebSocketController(
        val testRunnerEventBus: TestRunnerEventBus,
        val testsRunnerService: TestsRunnerService,
        val objectMapper: ObjectMapper) : TextWebSocketHandler(), TestRunnerEventListener {

    private val LOG = LoggerFactory.getLogger(TestsWebSocketController::class.java)

    var sessions: MutableMap<Long, SessionInfo> = mutableMapOf()

    var connectionOnExecutionId: Long? = null;
    var sessionOnExecution: WebSocketSession? = null

    init {
        testRunnerEventBus.addEventListener(this)
    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        val payload = message!!.payload;
        if (payload.startsWith("EXECUTE_TEST")) {
//TODO: allow multiple clients to trigger multiple tests in the same time but only one instance running at the same time

            val payloadWithoutPrefix = payload.removePrefix("EXECUTE_TEST-")
            val webSocketConnectionId: Long = payloadWithoutPrefix.substringBefore(";").toLong()
            val testModelsAsJson = payloadWithoutPrefix.substringAfter(";")
            val testModels: List<TestModel> = objectMapper.readValue<List<TestModel>>(testModelsAsJson)

            if (sessionOnExecution == null) {
                connectionOnExecutionId = webSocketConnectionId;
                sessionOnExecution = session;
            } else {
                sessions.put(webSocketConnectionId, SessionInfo(session!!, testModels));
            }

            testsRunnerService.executeTests(
                    testModels.map { it.path }
            )
        }
        if (payload.startsWith("CLOSE-")) {
            // todo: what should happen here? should we delete this if?
        }
    }

    private fun closeCurrentSession() {
        sessionOnExecution?.close()
        sessionOnExecution = null;
        connectionOnExecutionId = null;
    }

    override fun testSuiteEvent(runnerEvent: RunnerEvent) {
        val session = sessionOnExecution

        if (session != null && session.isOpen) {
            val eventAsString = objectMapper.writeValueAsString(runnerEvent)
            session.sendMessage(TextMessage(eventAsString))
        } else {
            LOG.warn("webSocket communication is closed")
        }

        if (runnerEvent is SuiteEndEvent ||
                runnerEvent is RunnerErrorEvent) {
            closeCurrentSession();
        }
    }
}

data class SessionInfo(
        val session: WebSocketSession,
        val testModels: List<TestModel>
)