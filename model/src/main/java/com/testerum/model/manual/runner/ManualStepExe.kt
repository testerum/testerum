package com.testerum.model.manual.runner

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.manual.enums.ManualTestStepStatus

data class ManualStepExe  @JsonCreator constructor(@JsonProperty("phase") val phase: StepPhaseEnum,
                                                   @JsonProperty("description") val description: String?,
                                                   @JsonProperty("stepStatus") val stepStatus: ManualTestStepStatus = ManualTestStepStatus.NOT_EXECUTED)