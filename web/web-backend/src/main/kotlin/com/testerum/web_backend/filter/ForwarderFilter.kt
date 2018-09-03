package com.testerum.web_backend.filter

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ForwarderFilter : Filter {

    companion object {
        private val SPLITTER = Regex("\\s+")
    }

    private lateinit var forwardToUrl: String
    private lateinit var ignoredUrls: List<Regex>

    override fun init(config: FilterConfig) {
        forwardToUrl = config.getForwardToUrl()
        ignoredUrls = config.getIgnoredUrls()
    }

    override fun destroy() {}

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
        val requestURI = request.requestURI

        if (ignoredUrls.matches(requestURI)) {
            chain.doFilter(request, response)
        } else {
            val requestDispatcher = request.getRequestDispatcher(forwardToUrl)
            requestDispatcher.forward(request, response)
        }
    }

    private fun List<Regex>.matches(text: String): Boolean = this.any { it.matches(text) }

    private fun FilterConfig.getForwardToUrl(): String = getParameter("forwardToUrl")

    private fun FilterConfig.getIgnoredUrls(): List<Regex> {
        val paramValue = getParameter("ignoredUrls")

        return SPLITTER.split(paramValue)
                .filter { it.isNotBlank() }
                .map { Regex(it) }
    }

    private fun FilterConfig.getParameter(name: String): String {
        return this.getInitParameter(name)
                ?: throw IllegalArgumentException("parameter [$name] is required")
    }

}
