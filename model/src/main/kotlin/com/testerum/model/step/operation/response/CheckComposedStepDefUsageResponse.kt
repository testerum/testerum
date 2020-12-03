package com.testerum.model.step.operation.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class CheckComposedStepDefUsageResponse @JsonCreator constructor(
    @JsonProperty("isUsed") val isUsed: Boolean = true,
    @JsonProperty("pathsForParentTests") val pathsForParentTests: List<Path> = emptyList(),
    @JsonProperty("pathsForDirectParentSteps") val pathsForDirectParentSteps: List<Path> = emptyList(),
    @JsonProperty("pathsForTransitiveParentSteps") val pathsForTransitiveParentSteps: List<Path> = emptyList()
)
