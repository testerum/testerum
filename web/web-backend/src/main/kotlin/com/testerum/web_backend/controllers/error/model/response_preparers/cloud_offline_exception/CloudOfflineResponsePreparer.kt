package com.testerum.web_backend.controllers.error.model.response_preparers.cloud_offline_exception

import com.testerum.cloud_client.CloudOfflineException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.validation.ValidationErrorResponse
import org.apache.http.HttpStatus
import org.springframework.http.ResponseEntity

class CloudOfflineResponsePreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
        val cloudClientException = exception as CloudOfflineException

        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body(
                        ValidationErrorResponse (
                                errorCode = ErrorCode.CLOUD_OFFLINE,
                                validationModel = ValidationModel(cloudClientException.message)
                        )
                )
    }

}
