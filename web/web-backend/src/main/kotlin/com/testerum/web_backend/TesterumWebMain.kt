package com.testerum.web_backend

import com.testerum.common_cmdline.banner.TesterumBanner
import com.testerum.web_backend.filter.AngularForwarderFilter
import com.testerum.web_backend.services.version_info.VersionInfoFrontendService
import org.eclipse.jetty.security.SecurityHandler
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.HandlerContainer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ErrorHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.StatisticsHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.slf4j.LoggerFactory
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.servlet.DispatcherServlet
import java.util.*
import javax.servlet.DispatcherType

object TesterumWebMain {

    private val LOG = LoggerFactory.getLogger(TesterumWebMain::class.java)
    private val PORT_SYSTEM_PROPERTY = "testerum.web.httpPort"

    @JvmStatic
    fun main(args: Array<String>) {
        AnsiConsole.systemInstall()

        val port = getPort()
        val server = createServer(port)

        server.start()

        val versionInfoFrontendService = VersionInfoFrontendService()
        val testerumVersion = versionInfoFrontendService.getVersionProperties()["projectVersion"] ?: ""

        val actualPort = (server.connectors[0] as ServerConnector).localPort

        TesterumBanner.BANNER.lines().forEach(LOG::info)

        LOG.info("Testerum (version $testerumVersion) is available at ${ansi().fgBlue()}http://localhost:$actualPort/${ansi().fgDefault()}")
        LOG.info("Press Ctrl+C to stop.")

        server.join()
    }

    private fun getPort(): Int {
        val syspropValue: String = System.getProperty(PORT_SYSTEM_PROPERTY) ?: "8080"

        return syspropValue.toIntOrNull()
                ?:throw IllegalArgumentException("port [$syspropValue] is not a valid number (given as system property [$PORT_SYSTEM_PROPERTY])")
    }

    private fun createServer(port: Int): Server {
        val server = Server(port)
        server.isDumpAfterStart = false
        server.isDumpBeforeStop = false

        // thread pool scheduler
        server.addBean(ScheduledExecutorScheduler())

        server.handler = createHandler(server)

        return server
    }

    private fun createHandler(server: Server): Handler {
        val handlerList = HandlerList()

        handlerList.addHandler(createWebAppContext(server))

        // StatisticsHandler must wrap all other handlers, in order for graceful shutdown to work
        val statisticsHandler = StatisticsHandler()
        statisticsHandler.handler = handlerList

        return statisticsHandler
    }

    private fun createWebAppContext(server: Server): Handler {
        val webAppContext = createEmptyWebAppContext(server)

        // add CharacterEncodingFilter
        webAppContext.addFilter(
                FilterHolder().apply {
                    filter = CharacterEncodingFilter()
                    name = CharacterEncodingFilter::class.java.simpleName.decapitalize()

                    initParameters["encoding"] = "UTF-8"
                },
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        )

        // add AngularForwarderFilter
        webAppContext.addFilter(
                FilterHolder().apply {
                    filter = AngularForwarderFilter()
                    name = AngularForwarderFilter::class.java.simpleName.decapitalize()

                    initParameters["forwardToUrl"] = "/index.html"
                },
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        )

        // add DispatcherServlet
        webAppContext.addServlet(
                ServletHolder().apply {
                    servlet = DispatcherServlet()
                    name = "springDispatcherServlet"

                    initParameters["contextConfigLocation"] = "classpath:/spring/spring_web.xml"

                    initOrder = 1
                },
                "/rest/*"
        )

        // add DefaultServlet, to serve static resources
        webAppContext.addServlet(
                ServletHolder().apply {
                    servlet = DefaultServlet()
                    name = "frontend-static-resources"

                    initParameters["resourceBase"] = this.javaClass.getResource("/frontend")?.toString()
                    initParameters["dirAllowed"] = "true"
                },
                "/"
        )


        // error page
        webAppContext.errorHandler = ErrorPageErrorHandler().apply {
            addErrorPage(java.lang.Exception::class.java, "/rest/error")
        }

        // enable WebSocket communication
        WebSocketServerContainerInitializer.configureContext(webAppContext)

        return webAppContext

    }

    private fun createEmptyWebAppContext(server: Server): ServletContextHandler {
        val parent: HandlerContainer? = null
        val contextPath = "/"
        val sessionHandler: SessionHandler? = null
        val securityHandler: SecurityHandler? = null
        val servletHandler: ServletHandler? = null
        val errorHandler: ErrorHandler? = null

        // the following line doesn't have any technical effect, but it makes the intention obvious
        val options = ServletContextHandler.NO_SESSIONS or ServletContextHandler.NO_SECURITY

        return ServletContextHandler(parent, contextPath, sessionHandler, securityHandler, servletHandler, errorHandler, options).apply {
            this.server = server
        }
    }

}

