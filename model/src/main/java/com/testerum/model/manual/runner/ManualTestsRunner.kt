package com.testerum.model.manual.runner

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.runner.enums.ManualTestsRunnerStatus
import java.time.LocalDateTime

data class ManualTestsRunner @JsonCreator constructor(
        @JsonProperty("path") val path: Path,
        @JsonProperty("environment") val environment: String?,
        @JsonProperty("applicationVersion") val applicationVersion: String?,
        @JsonProperty("createdDate") val createdDate: LocalDateTime?,
        @JsonProperty("finalizedDate") val finalizedDate: LocalDateTime?,
        @JsonProperty("status") val status: ManualTestsRunnerStatus = ManualTestsRunnerStatus.IN_EXECUTION,
        @JsonProperty("testsToExecute") val testsToExecute: List<ManualTestExe> = emptyList(),

        @JsonProperty("totalTests") val totalTests: Int = 0,
        @JsonProperty("passedTests") val passedTests: Int = 0,
        @JsonProperty("failedTests") val failedTests: Int = 0,
        @JsonProperty("blockedTests") val blockedTests: Int = 0,
        @JsonProperty("notApplicableTests") val notApplicableTests: Int = 0,
        @JsonProperty("notExecutedTests") val notExecutedTests: Int = 0
        )