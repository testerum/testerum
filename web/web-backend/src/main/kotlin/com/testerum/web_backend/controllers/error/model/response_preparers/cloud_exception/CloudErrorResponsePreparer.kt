package com.testerum.web_backend.controllers.error.model.response_preparers.cloud_exception

import com.testerum.licenses.cloud_client.CloudClientErrorResponseException
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.FullLogErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.validation.ValidationErrorResponse
import org.springframework.http.ResponseEntity

class CloudErrorResponsePreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
        val cloudClientException = exception as CloudClientErrorResponseException
        val error = cloudClientException.errorResponse.error

        return ResponseEntity.status(error.code)
                .body(
                        ValidationErrorResponse (
                                errorCode = ErrorCode.CLOUD_ERROR,
                                validationModel = ValidationModel(error.message)
                        )
                )
    }

}
