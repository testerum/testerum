package com.testerum.web_backend.controllers.error.model

import com.fasterxml.jackson.annotation.JsonProperty

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ErrorResponse(@JsonProperty("errorCode") val errorCode: ErrorCode)
