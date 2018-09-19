package com.testerum.web_backend.controllers.error.model.response_preparers.validation

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.exception.model.ValidationModel
import com.testerum.web_backend.controllers.error.model.ErrorCode
import com.testerum.web_backend.controllers.error.model.ErrorResponse

@Suppress("unused")
class ValidationErrorResponse(@JsonProperty("errorCode") errorCode: ErrorCode,
                              @JsonProperty("validationModel") val validationModel: ValidationModel): ErrorResponse(errorCode)
