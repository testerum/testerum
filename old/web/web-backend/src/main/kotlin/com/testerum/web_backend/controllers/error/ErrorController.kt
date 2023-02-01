package com.testerum.web_backend.controllers.error

import com.testerum.model.exception.ValidationException
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.generic.GenericErrorResponsePreparer
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.util.NestedServletException
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpServletRequest

@RequestMapping("/error")
class ErrorController(
    val errorResponsePreparerMap: Map<Class<out Throwable>, ErrorResponsePreparer<Throwable, ErrorResponse>>,
    val genericErrorResponsePreparer: GenericErrorResponsePreparer
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ErrorController::class.java)
    }

    @RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun errorPage(request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val exception: Throwable? = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) as Throwable?
        val nestedException: Throwable? = if (exception is NestedServletException && (exception.cause != null)) {
            exception.cause
        } else {
            exception
        }

        logException(request, nestedException)

        return getResponse(nestedException)
    }

    private fun logException(
        request: HttpServletRequest,
        exception: Throwable?
    ) {
        val originalRequestUri = request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) as String
        val originalStatusCode = request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE) as Int

        if (exception is ValidationException) {
            LOG.debug(
                "returning validation exception response" +
                    " for originalRequestUri=[{}]" +
                    ", originalStatusCode=[{}]" +
                    "; the stacktrace that follows is the original exception",
                originalRequestUri,
                originalStatusCode,
                exception
            )
            return
        }

        val message = buildString {
            append(
                "returning error response" +
                    " for originalRequestUri=[{}]" +
                    ", originalStatusCode=[{}]"
            )
            if (exception != null) {
                append("; the stacktrace that follows is the original exception")
            }
        }

        LOG.error(
            message,
            originalRequestUri,
            originalStatusCode,
            exception
        )
    }

    private fun getResponse(exception: Throwable?): ResponseEntity<ErrorResponse> {
        if (exception == null) {
            return genericErrorResponsePreparer.handleError(exception)
        }

        val responseFromPreparers = getResponseFromPreparers(exception)

        return responseFromPreparers
            ?: genericErrorResponsePreparer.handleError(exception)
    }

    private fun getResponseFromPreparers(exception: Throwable): ResponseEntity<ErrorResponse>? {
        val errorResponsePreparer = errorResponsePreparerMap[exception.javaClass]
            ?: return null

        return errorResponsePreparer.handleError(exception)
    }

}
