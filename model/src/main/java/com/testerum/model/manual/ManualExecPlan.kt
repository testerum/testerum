package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.runner.enums.ManualExecPlanStatus
import java.time.LocalDateTime

data class ManualExecPlan @JsonCreator constructor(
        @JsonProperty("path") val path: Path,
        @JsonProperty("oldPath") val oldPath: Path? = path,
        @JsonProperty("name") val name: String?,

        @JsonProperty("description") val description: String?,
        @JsonProperty("status") val status: ManualExecPlanStatus = ManualExecPlanStatus.IN_EXECUTION,
        @JsonProperty("createdDate") val createdDate: LocalDateTime?,
        @JsonProperty("finalizedDate") val finalizedDate: LocalDateTime?,

        @JsonProperty("manualTreeTests") val manualTreeTests: List<ManualTreeTest> = emptyList(),

        @JsonProperty("passedTests") val passedTests: Int = 0,
        @JsonProperty("failedTests") val failedTests: Int = 0,
        @JsonProperty("blockedTests") val blockedTests: Int = 0,
        @JsonProperty("notApplicableTests") val notApplicableTests: Int = 0,
        @JsonProperty("notExecutedOrInProgressTests") val notExecutedOrInProgressTests: Int = 0 // todo: rename in fronted also
) {

    @get:JsonProperty("totalTests")
    val totalTests: Int
        get() = passedTests + failedTests + blockedTests + notApplicableTests + notExecutedOrInProgressTests

}
