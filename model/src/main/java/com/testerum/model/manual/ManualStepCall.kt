package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.step.StepCall

data class ManualStepCall @JsonCreator constructor(@JsonProperty("stepCall") val stepCall: StepCall,
                                                   @JsonProperty("status") val status: ManualTestStepStatus)
