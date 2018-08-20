package com.testerum.model.step.operation.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class CheckComposedStepDefUpdateCompatibilityResponse @JsonCreator constructor(
        @JsonProperty("isCompatible") val isCompatible: Boolean = true,
        @JsonProperty("isUniqueStepPattern") val isUniqueStepPattern: Boolean = true,
        @JsonProperty("pathsForAffectedTests") val pathsForAffectedTests: List<Path> = emptyList(),
        @JsonProperty("pathsForDirectAffectedSteps") val pathsForDirectAffectedSteps: List<Path> = emptyList(),
        @JsonProperty("pathsForTransitiveAffectedSteps") val pathsForTransitiveAffectedSteps: List<Path> = emptyList()
) {
    companion object {
        val FULLY_COMPATIBLE = CheckComposedStepDefUpdateCompatibilityResponse(
                isCompatible = true,
                isUniqueStepPattern = true
        )
    }
}
