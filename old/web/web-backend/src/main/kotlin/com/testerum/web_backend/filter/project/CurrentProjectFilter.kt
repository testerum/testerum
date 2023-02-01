package com.testerum.web_backend.filter.project

import java.nio.file.Paths
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.file.Path as JavaPath

class CurrentProjectFilter : Filter {

    companion object {
        const val PROJECT_PATH_HEADER_NAME = "X-Testerum-Project"
        const val PROJECT_PATH_QUERY_PARAM_NAME = PROJECT_PATH_HEADER_NAME
    }

    override fun init(filterConfig: FilterConfig?) { }

    override fun destroy() { }

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
        try {
            val projectPath: JavaPath? = getProjectPath(request)

            ProjectDirHolder.set(projectPath, request.getFullRequestUrl())

            chain.doFilter(request, response)
        } finally {
            ProjectDirHolder.clear()
        }
    }

    private fun getProjectPath(request: HttpServletRequest): JavaPath? {
        return getProjectPathFromHeader(request)
                ?: getProjectPathFromQueryParameter(request)
    }

    private fun getProjectPathFromHeader(request: HttpServletRequest): JavaPath? {
        return request.getHeader(PROJECT_PATH_HEADER_NAME)?.let {
            Paths.get(it).toAbsolutePath().normalize()
        }
    }

    private fun getProjectPathFromQueryParameter(request: HttpServletRequest): JavaPath? {
        return request.getParameter(PROJECT_PATH_QUERY_PARAM_NAME)?.let {
            Paths.get(it).toAbsolutePath().normalize()
        }
    }

    private fun HttpServletRequest.getFullRequestUrl(): String {
        val result = requestURL

        val queryString = queryString

        if (queryString != null) {
            result.append("?").append(queryString)
        }

        return result.toString()
    }
}
