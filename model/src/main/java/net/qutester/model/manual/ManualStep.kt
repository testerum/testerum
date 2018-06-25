package net.qutester.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.manual.enums.ManualTestStepStatus

data class ManualStep  @JsonCreator constructor(@JsonProperty("phase") val phase: StepPhaseEnum,
                                                @JsonProperty("description") val description: String?)