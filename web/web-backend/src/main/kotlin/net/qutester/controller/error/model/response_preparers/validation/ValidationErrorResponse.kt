package net.qutester.controller.error.model.response_preparers.validation

import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.controller.error.model.ErrorCode
import net.qutester.controller.error.model.ErrorResponse
import net.qutester.exception.model.ValidationModel

class ValidationErrorResponse(@JsonProperty("errorCode") errorCode: ErrorCode,
                              @JsonProperty("validationModel") val validationModel: ValidationModel): ErrorResponse(errorCode)