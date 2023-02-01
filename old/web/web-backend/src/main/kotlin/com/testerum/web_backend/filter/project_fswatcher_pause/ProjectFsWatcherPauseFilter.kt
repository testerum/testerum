package com.testerum.web_backend.filter.project_fswatcher_pause

import com.testerum.web_backend.module_di.WebBackendModuleServiceLocator
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.concurrent.write

class ProjectFsWatcherPauseFilter : Filter {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectFsWatcherPauseFilter::class.java)

        private const val MAX_ERRORS_TO_LOG = 2
        private const val RESUME_TIME_AFTER_LAST_REQUEST_MILLIS = 5000L
    }

    private val projectFileSystemWatcher = WebBackendModuleServiceLocator.bootstrapper.webBackendModuleFactory.projectFileSystemWatcher

    private val concurrentRequestsCount = AtomicInteger(0)
    private val errorsLogged = AtomicInteger(0)
    private lateinit var scheduledExecutorService: ScheduledExecutorService

    private val resumeLock = ReentrantReadWriteLock()
    private var resumeFuture: ScheduledFuture<*>? = null

    override fun init(filterConfig: FilterConfig) {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

    override fun destroy() {
        this.scheduledExecutorService.shutdownNow()
    }

    override fun doFilter(request: ServletRequest,
                          response: ServletResponse,
                          chain: FilterChain) {
        doHttpFilter(
                request as HttpServletRequest,
                response as HttpServletResponse,
                chain
        )
    }

    private fun doHttpFilter(request: HttpServletRequest,
                             response: HttpServletResponse,
                             chain: FilterChain) {
        val concurrentRequestsIncludingThis = concurrentRequestsCount.incrementAndGet()
        if (concurrentRequestsIncludingThis == 1) {
            pauseSafely()
        }
        try {
            val requestLoggingInfo = getRequestLoggingInfo(request)

            LOG.debug("before doFilter: [$requestLoggingInfo]")
            chain.doFilter(request, response)
            LOG.debug("after doFilter: [$requestLoggingInfo]")
        } finally {
            val concurrentRequestsExcludingThis = concurrentRequestsCount.decrementAndGet()
            if (concurrentRequestsExcludingThis == 0) {
                scheduleResume()
            }
        }
    }

    private fun pauseSafely() {
        try {
            projectFileSystemWatcher.pause()
        } catch (e: Exception) {
            val errorsLoggedCount = errorsLogged.incrementAndGet()

            if (errorsLoggedCount < MAX_ERRORS_TO_LOG) {
                LOG.error("failed to pause projectFileSystemWatcher", e)
            }
        }
    }

    private fun scheduleResume() {
        resumeLock.write {
            resumeFuture?.cancel(true)

            resumeFuture = scheduledExecutorService.schedule(
                    { resume() },
                    RESUME_TIME_AFTER_LAST_REQUEST_MILLIS,
                    TimeUnit.MILLISECONDS
            )
        }
    }

    private fun resume() {
        while (concurrentRequestsCount.get() != 0) {
            Thread.sleep(100)
        }

        resumeSafely()
    }

    private fun resumeSafely() {
        try {
            projectFileSystemWatcher.resume()
        } catch (e: Exception) {
            val errorsLoggedCount = errorsLogged.incrementAndGet()

            if (errorsLoggedCount < MAX_ERRORS_TO_LOG) {
                LOG.error("failed to resume projectFileSystemWatcher", e)
            }
        }
    }

    private fun getRequestLoggingInfo(request: HttpServletRequest): String {
        val requestLoggingInfo = request.requestURL
        requestLoggingInfo.insert(0, request.method + " ")

        request.queryString?.let {
            requestLoggingInfo.append('?')
            requestLoggingInfo.append(it)
        }

        return requestLoggingInfo.toString()
    }
}
