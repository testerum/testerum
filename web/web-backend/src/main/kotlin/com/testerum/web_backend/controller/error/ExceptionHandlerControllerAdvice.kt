package com.testerum.web_backend.controller.error

import lombok.NonNull
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpServletRequest

/**
 * When any exception is thrown by a controller method, this class is invoked and will do a forward to the [ErrorController].
 */
@ControllerAdvice
class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun defaultErrorHandler(@NonNull request: HttpServletRequest,
                            e: Exception?): ModelAndView {
        request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e)

        return ModelAndView("/rest/error")
    }

}