package com.testerum.web_backend.controller.error.model

import com.fasterxml.jackson.annotation.JsonProperty

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ErrorResponse(@JsonProperty("errorCode") val errorCode: ErrorCode)