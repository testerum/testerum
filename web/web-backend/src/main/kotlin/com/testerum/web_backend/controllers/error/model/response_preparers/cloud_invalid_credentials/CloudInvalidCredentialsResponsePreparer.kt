package com.testerum.web_backend.controllers.error.model.response_preparers.cloud_invalid_credentials

import com.testerum.cloud_client.licenses.CloudInvalidCredentialsException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.validation.ValidationErrorResponse
import org.apache.http.HttpStatus
import org.springframework.http.ResponseEntity

class CloudInvalidCredentialsResponsePreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
        val cloudInvalidCredentialsException = exception as CloudInvalidCredentialsException

        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
                .body(
                        ValidationErrorResponse (
                                errorCode = ErrorCode.INVALID_CREDENTIALS,
                                validationModel = ValidationModel(cloudInvalidCredentialsException.message)
                        )
                )
    }

}
