package net.qutester.controller.error

import com.testerum.model.exception.ValidationException
import lombok.NonNull
import net.qutester.controller.error.model.ErrorResponse
import net.qutester.controller.error.model.response_preparers.ErrorResponsePreparer
import net.qutester.controller.error.model.response_preparers.generic.GenericErrorResponsePreparer
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.util.NestedServletException
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpServletRequest

@RequestMapping("/error")
class ErrorController(val errorResponsePreparerMap: Map<Class<out Throwable>, ErrorResponsePreparer<Throwable, ErrorResponse>>,
                      val genericErrorResponsePreparer: GenericErrorResponsePreparer) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ErrorController::class.java)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun errorPage(@NonNull request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val exception = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) as Throwable?
        val nestedException = if (exception is NestedServletException) exception.cause else exception

        logException(request, nestedException)

        return getResponse(nestedException as Throwable)
    }

    private fun logException(@NonNull request: HttpServletRequest,
                             exception: Throwable?) {
        val originalRequestUri = request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) as String
        val originalStatusCode = request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE) as Int

        if (exception is ValidationException) {
            LOGGER.debug(
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

        LOGGER.error(
                "returning error response" +
                        " for originalRequestUri=[{}]" +
                        ", originalStatusCode=[{}]" +
                        "; the stacktrace that follows is the original exception",
                originalRequestUri,
                originalStatusCode,
                exception
        )
    }

    private fun getResponse(exception: Throwable): ResponseEntity<ErrorResponse> {
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