package com.testerum.web_backend.controllers.error.model.response_preparers.cloud_exception

import com.testerum.licenses.cloud_client.CloudClientErrorResponseException
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.FullLogErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import org.springframework.http.ResponseEntity

class CloudErrorResponsePreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
        val cloudClientException = exception as CloudClientErrorResponseException
        val error = cloudClientException.errorResponse.error

        return ResponseEntity.status(error.code)
                .body(
                        FullLogErrorResponse(
                                errorCode = ErrorCode.CLOUD_ERROR,
                                uiMessage = error.message,
                                exceptionAsString = ""
                        )
                )
    }

}
