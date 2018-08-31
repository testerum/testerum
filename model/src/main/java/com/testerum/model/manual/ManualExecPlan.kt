package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.runner.enums.ManualExecPlanStatus
import com.testerum.model.test.TestModel
import java.time.LocalDateTime

data class ManualExecPlan @JsonCreator constructor(
        @JsonProperty("path") val path: Path,
        @JsonProperty("oldPath") val oldPath: Path? = path,
        @JsonProperty("environment") val environment: String?,
        @JsonProperty("applicationVersion") val applicationVersion: String?,
        @JsonProperty("description") val description: String?,
        @JsonProperty("status") val status: ManualExecPlanStatus = ManualExecPlanStatus.IN_EXECUTION,
        @JsonProperty("createdDate") val createdDate: LocalDateTime?,
        @JsonProperty("finalizedDate") val finalizedDate: LocalDateTime?,

        @JsonProperty("manualTreeTests") val manualTreeTests: List<ManualTreeTest> = emptyList(),

        @JsonProperty("totalTests") val totalTests: Int = 0,

        @JsonProperty("passedTests") val passedTests: Int = 0,
        @JsonProperty("failedTests") val failedTests: Int = 0,
        @JsonProperty("blockedTests") val blockedTests: Int = 0,
        @JsonProperty("notApplicableTests") val notApplicableTests: Int = 0,
        @JsonProperty("notExecutedTests") val notExecutedTests: Int = 0
)
