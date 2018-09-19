package com.testerum.web_backend.controllers.error.model.response_preparers

import com.testerum.web_backend.controllers.error.model.ErrorResponse
import org.springframework.http.ResponseEntity

interface ErrorResponsePreparer<E : Throwable, R : ErrorResponse> {

    fun handleError(exception: E): ResponseEntity<R>

}
