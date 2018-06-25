package net.qutester.controller.error.model

class FullLogErrorResponse(errorCode: ErrorCode,
                           val uiMessage: String?,
                           val exceptionAsString: String): ErrorResponse(errorCode) {
}