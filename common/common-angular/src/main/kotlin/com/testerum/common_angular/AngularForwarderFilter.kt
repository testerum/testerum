package com.testerum.common_angular

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AngularForwarderFilter(
    private val forwardToUrl: String,
    customIgnoredUrls: List<Regex>
) : Filter {

    companion object {
        private val COMMON_ANGULAR_IGNORED_URLS = listOf(
            // JavaScript
            matchesFileExtension(".js"),

            // CSS
            matchesFileExtension(".css"),

            // images
            matchesFileExtension(".ico"),
            matchesFileExtension(".png"),
            matchesFileExtension(".gif"),
            matchesFileExtension(".jpg"),
            matchesFileExtension(".jpeg"),
            matchesFileExtension(".svg"),

            // fonts
            matchesFileExtension(".ttf"),
            matchesFileExtension(".eot"),
            matchesFileExtension(".woff"),
            matchesFileExtension(".woff2"),

            // source maps
            matchesFileExtension(".map")
        )
    }

    private val ignoredUrls: List<Regex> = run {
        val result = mutableListOf<Regex>()

        result.addAll(COMMON_ANGULAR_IGNORED_URLS)
        result.addAll(customIgnoredUrls)

        return@run result
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        doHttpFilter(
            request as HttpServletRequest,
            response as HttpServletResponse,
            chain
        )
    }

    private fun doHttpFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val requestUri = request.requestURI

        if (shouldForward(requestUri)) {
            val requestDispatcher = request.getRequestDispatcher(forwardToUrl)
            requestDispatcher.forward(request, response)
        } else {
            chain.doFilter(request, response)
        }
    }

    override fun init(config: FilterConfig) {}

    override fun destroy() {}


    private fun shouldForward(requestUri: String): Boolean = !shouldIgnore(requestUri)

    private fun shouldIgnore(requestUri: String): Boolean {
        return ignoredUrls.any {
            it.matches(requestUri)
        }
    }

}
