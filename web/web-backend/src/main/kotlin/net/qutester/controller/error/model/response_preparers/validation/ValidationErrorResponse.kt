package net.qutester.controller.error.model.response_preparers.validation

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.exception.model.ValidationModel
import net.qutester.controller.error.model.ErrorCode
import net.qutester.controller.error.model.ErrorResponse

@Suppress("unused")
class ValidationErrorResponse(@JsonProperty("errorCode") errorCode: ErrorCode,
                              @JsonProperty("validationModel") val validationModel: ValidationModel): ErrorResponse(errorCode)
