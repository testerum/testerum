package net.qutester.controller.error.model

import com.fasterxml.jackson.annotation.JsonProperty

open class ErrorResponse(@JsonProperty("errorCode") val errorCode: ErrorCode)