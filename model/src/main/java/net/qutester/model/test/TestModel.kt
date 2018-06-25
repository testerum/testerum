package net.qutester.model.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.StepCall
import net.qutester.util.indent


data class TestModel @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                              @JsonProperty("text") val text: String, // todo: rename to "name"
                                              @JsonProperty("description") val description: String?,
                                              @JsonProperty("stepCalls") val stepCalls: List<StepCall> = emptyList()) {

    private val _id = path.toString()

    val id: String
        get() = _id

    override fun toString() = text

    fun toDebugTree() = buildString { toDebugTree(this, 0) }

    fun toDebugTree(destination: StringBuilder,
                    indentLevel: Int) {
        destination.indent(indentLevel)
        destination.append("TEST: ").append(text).append("\n")

        for (stepCall in stepCalls) {
            stepCall.toDebugTree(destination, indentLevel + 1)
        }
    }

}