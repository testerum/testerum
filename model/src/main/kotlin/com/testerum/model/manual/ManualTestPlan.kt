package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import java.time.LocalDateTime

data class ManualTestPlan @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("path") val path: Path,
        @JsonProperty("oldPath") val oldPath: Path? = path,

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

    @JsonIgnore
    fun getNewPath(): Path {
        val directories = path.directories
        val newDirectories = mutableListOf<String>()

        if (directories.isNotEmpty()) {
            newDirectories.addAll(
                    directories.subList(0, directories.size - 1)
            )
        }
        newDirectories.add(name)

        return path.copy(
                directories = newDirectories
        )
    }
}
