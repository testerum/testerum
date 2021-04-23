package com.testerum.report_server

import com.testerum.logging.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class ApplicationStartupInfoLogger(
    @Value("\${server.port}") private val serverPort: Int,
    @Value("\${server.servlet.context-path}") private val serverContextPath: String,
) : ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val LOGGER = getLogger()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val prettyPort = if (serverPort != 80) {
            ":$serverPort"
        } else {
            ""
        }

        LOGGER.info("")
        LOGGER.info("Testerum Report Server is accessible at http://localhost${prettyPort}${serverContextPath}")
    }
}
