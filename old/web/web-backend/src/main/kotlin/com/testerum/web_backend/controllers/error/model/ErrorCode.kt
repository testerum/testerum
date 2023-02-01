package com.testerum.web_backend.controllers.error.model

enum class ErrorCode {
    GENERIC_ERROR,
    VALIDATION,
    ILLEGAL_FILE_OPERATION,
    CLOUD_ERROR,
    CLOUD_OFFLINE,
    INVALID_CREDENTIALS, // invalid username/password combination
    NO_VALID_LICENSE,    // the user exists, but he/she doesn't have any valid license assigned
    ;
}
