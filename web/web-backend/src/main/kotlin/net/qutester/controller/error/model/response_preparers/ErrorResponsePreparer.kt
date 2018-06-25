package net.qutester.controller.error.model.response_preparers

import net.qutester.controller.error.model.ErrorResponse
import org.springframework.http.ResponseEntity

interface ErrorResponsePreparer<E : Throwable, R : ErrorResponse> {

    fun handleError(exception: E): ResponseEntity<R>
}