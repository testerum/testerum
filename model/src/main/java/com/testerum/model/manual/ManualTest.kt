package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.step.StepCall

data class ManualTest @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                               @JsonProperty("oldPath") val oldPath: Path? = path,
                                               @JsonProperty("name") val name: String,
                                               @JsonProperty("description") val description: String?,
                                               @JsonProperty("tags") val tags: List<String> = emptyList(),
                                               @JsonProperty("stepsStatus") val stepsStatus: List<ManualTestStepStatus> = emptyList(),
                                               @JsonProperty("stepCalls") val stepCalls: List<StepCall> = emptyList(),
                                               @JsonProperty("status") val status: ManualTestStatus,
                                               @JsonProperty("comments") val comments: String?,
                                               @JsonProperty("isTestPlanFinalized") val isTestPlanFinalized: Boolean?
                                               )
