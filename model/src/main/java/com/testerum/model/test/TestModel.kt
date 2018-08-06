package com.testerum.model.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.common_kotlin.indent
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.StepCall
import com.testerum.model.warning.Warning


data class TestModel @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                              @JsonProperty("properties") val properties: TestProperties,
                                              @JsonProperty("text") val text: String, // todo: rename to "name"
                                              @JsonProperty("description") val description: String?,
                                              @JsonProperty("tags") val tags: List<String> = emptyList(),
                                              @JsonProperty("stepCalls") val stepCalls: List<StepCall> = emptyList(),
                                              @JsonProperty("warnings") val warnings: List<Warning> = emptyList()) {

    private val _id = path.toString()

    @get:JsonProperty("id")
    val id: String
        get() = _id

    private val _descendantsHaveWarnings: Boolean = stepCalls.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }

    @get:JsonProperty("descendantsHaveWarnings")
    val descendantsHaveWarnings: Boolean
        get() = _descendantsHaveWarnings


    @get:JsonIgnore
    val hasOwnOrDescendantWarnings: Boolean
        get() = warnings.isNotEmpty() || descendantsHaveWarnings

    override fun toString() = "TestModel(name=$text, path=$path)"

    @Suppress("unused")
    fun toDebugTree() = buildString { toDebugTree(this, 0) }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toDebugTree(destination: StringBuilder,
                    indentLevel: Int) {
        destination.indent(indentLevel)

        destination.append("TEST: ").append(text).append("\n")

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