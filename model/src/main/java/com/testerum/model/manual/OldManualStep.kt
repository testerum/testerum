package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.enums.StepPhaseEnum

data class OldManualStep  @JsonCreator constructor(@JsonProperty("phase") val phase: StepPhaseEnum,
                                                   @JsonProperty("description") val description: String?)