package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.text.StepPattern

data class ReportUndefinedStepDef @JsonCreator constructor(@JsonProperty("id")          override val id: String,
                                                           @JsonProperty("phase")       override val phase: StepPhaseEnum,
                                                           @JsonProperty("stepPattern") override val stepPattern: StepPattern) : ReportStepDef
