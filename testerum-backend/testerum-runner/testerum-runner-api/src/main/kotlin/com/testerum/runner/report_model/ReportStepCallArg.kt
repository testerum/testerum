package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ReportStepCallArg @JsonCreator constructor(@JsonProperty("name") val name: String?,
                                                 @JsonProperty("content") val content: String?,
                                                 @JsonProperty("type") val type: String,
                                                 @JsonProperty("path") val path: String?)
