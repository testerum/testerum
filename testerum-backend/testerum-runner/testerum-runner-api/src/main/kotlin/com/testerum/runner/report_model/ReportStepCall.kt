package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ReportStepCall @JsonCreator constructor(@JsonProperty("id")        val id: String,
                                                   @JsonProperty("stepDefId") val stepDefId: String,
                                                   @JsonProperty("args")      val args: List<ReportStepCallArg>)
