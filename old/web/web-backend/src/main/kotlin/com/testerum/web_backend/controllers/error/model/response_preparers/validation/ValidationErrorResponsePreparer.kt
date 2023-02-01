package com.testerum.web_backend.controllers.error.model.response_preparers.validation

import com.testerum.model.exception.ValidationException
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class ValidationErrorResponsePreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
        val validationException = exception as ValidationException

        val validationErrorResponse = ValidationErrorResponse(
                ErrorCode.VALIDATION,
                validationException.validationModel
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        validationErrorResponse
                )
    }

}
