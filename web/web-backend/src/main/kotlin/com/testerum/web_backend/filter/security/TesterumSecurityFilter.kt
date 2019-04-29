package com.testerum.web_backend.filter.security

import com.testerum.model.exception.model.ValidationModel
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.response_preparers.validation.ValidationErrorResponse
import com.testerum.web_backend.module_di.WebBackendModuleServiceLocator
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TesterumSecurityFilter : Filter {

    companion object {
        private val ignoredUrls: List<Regex> = listOf(
                Regex("^/rest/user/license-info.*"),
                Regex("^/rest/user/login/credentials.*"),
                Regex("^/rest/user/login/file.*"),
                Regex("^/rest/messages.*"),
                Regex("^/rest/project-reloaded-ws.*"),
                Regex("^/rest/tests-ws.*")
        )
    }

    private val licensesCache = WebBackendModuleServiceLocator.bootstrapper.webBackendModuleFactory.licensesCache
    private val restApiObjectMapper = WebBackendModuleServiceLocator.bootstrapper.webBackendModuleFactory.restApiObjectMapper

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
        if (shouldIgnore(request)) {
            chain.doFilter(request, response)
            return
        }

        val hasAtLeastOneLicense = licensesCache.hasAtLeastOneLicense()
        if (!hasAtLeastOneLicense) {
            chain.doFilter(request, response)
            return
        }

        val currentUser = CurrentUserHolder.get()

        if (currentUser != null) {
            chain.doFilter(request, response)
            return
        }

        val errorResponse = ValidationErrorResponse(
                errorCode = ErrorCode.INVALID_CREDENTIALS,
                validationModel = ValidationModel("Testerum is in licensed mode, and there is no current user (authentication token missing or invalid).")
        )

        val serializedErrorResponse = restApiObjectMapper.writeValueAsString(errorResponse)

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(serializedErrorResponse)
    }

    private fun shouldIgnore(request: HttpServletRequest): Boolean {
        val requestUri = request.requestURI

        return ignoredUrls.any {
            it.matches(requestUri)
        }
    }

}
