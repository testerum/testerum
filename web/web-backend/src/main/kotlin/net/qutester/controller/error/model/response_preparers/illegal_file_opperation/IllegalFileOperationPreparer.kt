package net.qutester.controller.error.model.response_preparers.illegal_file_opperation

import net.qutester.controller.error.model.ErrorCode
import net.qutester.controller.error.model.ErrorResponse
import net.qutester.controller.error.model.FullLogErrorResponse
import net.qutester.controller.error.model.response_preparers.ErrorResponsePreparer
import net.testerum.common.jdk.throwable.toStringWithStacktrace
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