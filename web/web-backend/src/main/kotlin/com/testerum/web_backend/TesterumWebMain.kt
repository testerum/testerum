package com.testerum.web_backend

import com.testerum.common_cmdline.banner.TesterumBanner
import com.testerum.web_backend.filter.angular_forwarder.AngularForwarderFilter
import com.testerum.web_backend.filter.cache.DisableCacheFilter
import com.testerum.web_backend.filter.project.ProjectFilter
import com.testerum.web_backend.filter.project_fswatcher_pause.ProjectFsWatcherPauseFilter
import com.testerum.web_backend.services.version_info.VersionInfoFrontendService
import org.eclipse.jetty.security.SecurityHandler
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.HandlerContainer
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ErrorHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.StatisticsHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ErrorPageErrorHandler
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.slf4j.LoggerFactory
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.servlet.DispatcherServlet
import java.util.EnumSet
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
        val testerumVersionGitRevision = versionInfoFrontendService.getVersionProperties()["gitRevision"] ?: ""
        val testerumVersionTimestamp = versionInfoFrontendService.getVersionProperties()["buildTimestamp"] ?: ""

        val actualPort = (server.connectors[0] as ServerConnector).localPort

        TesterumBanner.BANNER.lines().forEach(LOG::info)

        val versionInfo = if (testerumVersion == "develop-SNAPSHOT") {
            buildString {
                append(testerumVersion)

                if (testerumVersionGitRevision.isNotBlank()) {
                    append(", git $testerumVersionGitRevision")
                }
                if (testerumVersionTimestamp.isNotBlank()) {
                    append(", built $testerumVersionTimestamp")
                }
            }
        } else {
            testerumVersion
        }

        LOG.info("Testerum server started.")
        LOG.info("Testerum (version $versionInfo) is available at ${ansi().bg(Ansi.Color.BLACK).fgBrightGreen()}http://localhost:$actualPort/${ansi().bgDefault().fgDefault()}")
        LOG.info("Press Ctrl+C to stop.")

        server.join()
    }

    private fun getPort(): Int {
        val syspropValue: String = System.getProperty(PORT_SYSTEM_PROPERTY) ?: "9999"

        return syspropValue.toIntOrNull()
                ?: throw IllegalArgumentException("port [$syspropValue] is not a valid number (given as system property [$PORT_SYSTEM_PROPERTY])")
    }

    private fun createServer(port: Int): Server {
        val server = Server(port)
        server.isDumpAfterStart = false
        server.isDumpBeforeStop = false

        // thread pool scheduler
        server.addBean(ScheduledExecutorScheduler())

        server.handler = createHandler(server)

        doNotReturnSensitiveHeaders(server)

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

        // add DisableCacheFilter
        webAppContext.addFilter(
                FilterHolder().apply {
                    filter = DisableCacheFilter()
                    name = DisableCacheFilter::class.java.simpleName.decapitalize()
                },
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        )

        // add ProjectFilter
        webAppContext.addFilter(
                FilterHolder().apply {
                    filter = ProjectFilter()
                    name = ProjectFilter::class.java.simpleName.decapitalize()
                },
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        )

        // add ProjectFsWatcherPauseFilter
        webAppContext.addFilter(
                FilterHolder().apply {
                    filter = ProjectFsWatcherPauseFilter()
                    name = ProjectFsWatcherPauseFilter::class.java.simpleName.decapitalize()
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

    private fun doNotReturnSensitiveHeaders(server: Server) {
        // improve security: make sure we are not sending the "Server" and "X-Powered-By" response headers
        for (connector in server.connectors) {
            for (connectionFactory in connector.connectionFactories) {
                if (connectionFactory is HttpConnectionFactory) {
                    connectionFactory.httpConfiguration.sendServerVersion = false
                    connectionFactory.httpConfiguration.sendXPoweredBy = false
                }
            }
        }
    }

}

