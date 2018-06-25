package com.testerum.scanner.step_lib_scanner.model.hooks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HookDef @JsonCreator constructor(
        @JsonProperty("phase") val phase: HookPhase,
        @JsonProperty("className") val className: String,
        @JsonProperty("methodName") val methodName: String,
        @JsonProperty("order") val order: Int,
        @JsonProperty("description") val description: String? = null
)
