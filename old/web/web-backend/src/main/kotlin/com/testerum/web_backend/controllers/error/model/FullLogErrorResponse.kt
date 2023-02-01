package com.testerum.web_backend.controllers.error.model

@Suppress("MemberVisibilityCanBePrivate", "unused")
class FullLogErrorResponse(errorCode: ErrorCode,
                           val uiMessage: String?,
                           val exceptionAsString: String): ErrorResponse(errorCode)
