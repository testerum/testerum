package com.testerum.model.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.common_kotlin.indent
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.StepCall
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.warning.Warning

// todo: rename class to "Test"
data class TestModel @JsonCreator constructor(@JsonProperty("name") val name: String,
                                              @JsonProperty("path") override val path: Path,
                                              @JsonProperty("oldPath") val oldPath: Path? = path,
                                              @JsonProperty("properties") val properties: TestProperties,
                                              @JsonProperty("description") val description: String?,
                                              @JsonProperty("tags") val tags: List<String> = emptyList(),
                                              @JsonProperty("scenarios") val scenarios: List<Scenario> = emptyList(),
                                              @JsonProperty("afterHooks") val afterHooks: List<StepCall> = emptyList(),
                                              @JsonProperty("stepCalls") val stepCalls: List<StepCall> = emptyList(),
                                              @JsonProperty("warnings") val warnings: List<Warning> = emptyList()): HasPath {

    companion object {
        val TEST_FILE_EXTENSION = "test"
    }

    private val _id = path.toString()

    @get:JsonProperty("id")
    val id: String
        get() = _id

    private val _descendantsHaveWarnings: Boolean = stepCalls.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }
                                                 || afterHooks.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }

    @get:JsonProperty("descendantsHaveWarnings")
    val descendantsHaveWarnings: Boolean
        get() = _descendantsHaveWarnings


    @get:JsonIgnore
    val hasOwnOrDescendantWarnings: Boolean
        get() = warnings.isNotEmpty() || descendantsHaveWarnings

    @JsonIgnore
    fun getNewPath(): Path {
        return path.copy(
                fileName = name,
                fileExtension = TEST_FILE_EXTENSION
        )
    }

    override fun toString() = "TestModel(name=$name, path=$path)"

    @Suppress("unused")
    fun toDebugTree() = buildString { toDebugTree(this, 0) }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toDebugTree(destination: StringBuilder,
                    indentLevel: Int) {
        destination.indent(indentLevel)

        destination.append("TEST: ").append(name).append("\n")

        if (!properties.isEmpty()) {
            destination.append("test-properties: <<")
            if (properties.isManual) {
                destination.append("manual")
            }
            if (properties.isDisabled) {
                destination.append("disabled")
            }
            destination.append(">>\n")
        }

        for (stepCall in stepCalls) {
            stepCall.toDebugTree(destination, indentLevel + 1)
        }
    }

}
