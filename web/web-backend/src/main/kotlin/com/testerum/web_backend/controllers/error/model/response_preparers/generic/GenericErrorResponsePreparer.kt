package com.testerum.web_backend.controllers.error.model.response_preparers.generic

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.FullLogErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class GenericErrorResponsePreparer{

    fun handleError(exception: Throwable?): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        FullLogErrorResponse(
                                ErrorCode.GENERIC_ERROR,
                                "Server Exception",
                                exception.toStringWithStacktrace()
                        )
                )
    }
}
