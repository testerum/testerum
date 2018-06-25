package net.qutester.controller.error.model.response_preparers.generic

import net.qutester.controller.error.model.ErrorResponse
import net.qutester.controller.error.model.ErrorCode
import net.qutester.controller.error.model.FullLogErrorResponse
import net.qutester.controller.error.model.response_preparers.ErrorResponsePreparer
import net.testerum.common.jdk.throwable.toStringWithStacktrace
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class GenericErrorResponsePreparer : ErrorResponsePreparer<Throwable, ErrorResponse> {

    override fun handleError(exception: Throwable): ResponseEntity<ErrorResponse> {
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