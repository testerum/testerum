package com.testerum.web_backend.controllers.error.model.response_preparers.illegal_file_opperation

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse
import com.testerum.web_backend.controllers.error.model.FullLogErrorResponse
import com.testerum.web_backend.controllers.error.model.response_preparers.ErrorResponsePreparer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class IllegalFileOperationPreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        FullLogErrorResponse(
                                ErrorCode.ILLEGAL_FILE_OPERATION,
                                exception.message,
                                exception.toStringWithStacktrace()
                        )
                )
    }
}
