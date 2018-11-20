package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import java.time.LocalDateTime

data class ManualTestPlan @JsonCreator constructor(
        @JsonProperty("path") val path: Path,
        @JsonProperty("oldPath") val oldPath: Path? = path,
        @JsonProperty("name") val name: String?,

        @JsonProperty("description") val description: String?,
        @JsonProperty("isFinalized") val isFinalized: Boolean = false,
        @JsonProperty("createdDate") val createdDate: LocalDateTime?,
        @JsonProperty("finalizedDate") val finalizedDate: LocalDateTime?,

        @JsonProperty("manualTreeTests") val manualTreeTests: List<ManualTreeTest> = emptyList(),

        @JsonProperty("passedTests") val passedTests: Int = 0,
        @JsonProperty("failedTests") val failedTests: Int = 0,
        @JsonProperty("blockedTests") val blockedTests: Int = 0,
        @JsonProperty("notApplicableTests") val notApplicableTests: Int = 0,
        @JsonProperty("notExecutedOrInProgressTests") val notExecutedOrInProgressTests: Int = 0
) {

    @get:JsonProperty("totalTests")
    val totalTests: Int
        get() = passedTests + failedTests + blockedTests + notApplicableTests + notExecutedOrInProgressTests

}
