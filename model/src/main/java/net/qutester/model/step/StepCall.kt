package net.qutester.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.common_kotlin.indent
import net.qutester.model.arg.Arg
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.text.parts.TextStepPatternPart
import net.qutester.model.warning.Warning

data class StepCall @JsonCreator constructor(
        @JsonProperty("id") val id: String,
        @JsonProperty("stepDef") val stepDef: StepDef,
        @JsonProperty("args") val args: List<Arg>,
        @JsonProperty("warnings") val warnings: List<Warning> = emptyList()
) {

    private val _descendantsHaveWarnings: Boolean
            = ((stepDef is ComposedStepDef) && (stepDef.hasOwnOrDescendantWarnings))
              || args.any { it.hasOwnOrDescendantWarnings }

    @get:JsonProperty("descendantsHaveWarnings")
    val descendantsHaveWarnings: Boolean
        get() = _descendantsHaveWarnings

    @get:JsonIgnore
    val hasOwnOrDescendantWarnings: Boolean
        get() = warnings.isNotEmpty() || descendantsHaveWarnings


    override fun toString() = buildString { toString(this, 0) }

    private fun toString(destination: StringBuilder,
                         indentLevel: Int) {
        destination.indent(indentLevel)

        val stepType: String = when (stepDef) {
            is UndefinedStepDef -> "UNDEFINED"
            is BasicStepDef     -> "BASIC"
            is ComposedStepDef  -> "COMPOSED"
            else                -> stepDef.javaClass.simpleName
        }
        destination.append(stepType).append(": ").append(stepDef.phase).append(" ")

        var argIndex = 0
        for (patternPart in stepDef.stepPattern.patternParts) {
            when (patternPart) {
                is TextStepPatternPart -> destination.append(patternPart.text)
                is ParamStepPatternPart -> {
                    destination.append("<<")

                    if (patternPart.name.isNotBlank()) {
                        destination.append(patternPart.name).append(" = ")
                    }

                    val arg = args[argIndex]

                    // todo: escape ">>" (should we also escape "<<"?)
                    if (arg.path == null) {
                        destination.append(arg.content.orEmpty())
                    } else {
                        destination.append("file:").append(arg.path)
                    }

                    destination.append(">>")

                    argIndex++
                }
            }
        }
    }

    fun toDebugTree() = buildString { toDebugTree(this, 0) }

    fun toDebugTree(destination: StringBuilder,
                    indentLevel: Int) {
        toString(destination, indentLevel)
        destination.append("\n")

        if (stepDef is ComposedStepDef) {
            for (stepCall in stepDef.stepCalls) {
                stepCall.toDebugTree(destination, indentLevel + 1)
            }
        }
    }

}