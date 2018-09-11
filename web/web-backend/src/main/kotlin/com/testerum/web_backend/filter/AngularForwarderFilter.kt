package com.testerum.web_backend.filter

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AngularForwarderFilter : Filter {

    private lateinit var forwardToUrl: String

    override fun init(config: FilterConfig) {
        forwardToUrl = config.getParameter("forwardToUrl")
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

        if (AngularForwardingSelector.shouldForward(requestURI)) {
            val requestDispatcher = request.getRequestDispatcher(forwardToUrl)
            requestDispatcher.forward(request, response)
        } else {
            chain.doFilter(request, response)
        }
    }


    private fun FilterConfig.getParameter(name: String): String {
        return this.getInitParameter(name)
                ?: throw IllegalArgumentException("parameter [$name] is required")
    }

}
