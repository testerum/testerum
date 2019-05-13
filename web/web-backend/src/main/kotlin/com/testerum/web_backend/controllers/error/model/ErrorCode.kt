package com.testerum.web_backend.controllers.error.model

enum class ErrorCode {
    GENERIC_ERROR,
    VALIDATION,
    ILLEGAL_FILE_OPERATION,
    CLOUD_ERROR,
    CLOUD_OFFLINE,
    INVALID_CREDENTIALS, // invalid username/password combination, or invalid licence file
    ;
}
