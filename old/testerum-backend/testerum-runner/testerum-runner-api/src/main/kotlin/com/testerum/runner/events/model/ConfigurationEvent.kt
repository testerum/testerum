package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.tests_finder.TestPath
import java.time.LocalDateTime

data class ConfigurationEvent @JsonCreator constructor(
    @JsonProperty("time")         override val time: LocalDateTime = LocalDateTime.now(),
    @JsonProperty("eventKey")     override val eventKey: String,
    @JsonProperty("projectId")             val projectId: String,
    @JsonProperty("projectName")           val projectName: String,
    @JsonProperty("verbose")               val verbose: Boolean,
    @JsonProperty("repositoryDirectory")   val repositoryDirectory: String,
    @JsonProperty("variablesEnvironment")  val variablesEnvironment: String?,
    @JsonProperty("variableOverrides")     val variableOverrides: Map<String, String>,
    @JsonProperty("settingsFile")          val settingsFile: String?,
    @JsonProperty("settingOverrides")      val settingOverrides: Map<String, String>,
    @JsonProperty("testPaths")             val testPaths: List<TestPath>,
    @JsonProperty("tagsToInclude")         val tagsToInclude: List<String>,
    @JsonProperty("tagsToExclude")         val tagsToExclude: List<String>,
    @JsonProperty("reportsWithProperties") val reportsWithProperties: List<String>,
    @JsonProperty("managedReportsDir")     val managedReportsDir: String?,
    @JsonProperty("executionName")         val executionName: String?
): RunnerEvent
