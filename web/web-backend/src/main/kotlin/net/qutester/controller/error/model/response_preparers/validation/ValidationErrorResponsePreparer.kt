package net.qutester.controller.error.model.response_preparers.validation

import net.qutester.controller.error.model.ErrorResponse
import net.qutester.controller.error.model.ErrorCode
import net.qutester.controller.error.model.response_preparers.ErrorResponsePreparer
import net.qutester.exception.ValidationException
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