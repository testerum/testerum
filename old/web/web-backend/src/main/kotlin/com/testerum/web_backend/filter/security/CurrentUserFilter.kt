package com.testerum.web_backend.filter.security

import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import com.testerum.web_backend.module_di.WebBackendModuleServiceLocator
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CurrentUserFilter : Filter {

    companion object {
        const val TESTERUM_AUTH_HEADER_NAME = "X-Testerum-Auth"
    }

    private val authTokenService = WebBackendModuleServiceLocator.bootstrapper.webBackendModuleFactory.authTokenService

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
            val currentUser: LicensedUserProfile? = getCurrentUser(request)

            CurrentUserHolder.set(currentUser)

            chain.doFilter(request, response)
        } finally {
            CurrentUserHolder.clear()
        }
    }

    private fun getCurrentUser(request: HttpServletRequest): LicensedUserProfile? {
        val authToken = request.getHeader(TESTERUM_AUTH_HEADER_NAME)
                ?: return null

        return authTokenService.authenticate(authToken)
    }

}
