package com.testerum.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.common_kotlin.indent
import com.testerum.model.arg.Arg
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.warning.Warning

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


    @JsonIgnore
    fun isCallOf(otherDef: StepDef): Boolean {
        // phase
        if (stepDef.phase != otherDef.phase) {
            return false
        }

        // pattern
        val thisPatternParts = stepDef.stepPattern.patternParts
        val otherPatternParts = otherDef.stepPattern.patternParts

        if (thisPatternParts.size != otherPatternParts.size) {
            return false
        }

        for ((i, thisPart) in thisPatternParts.withIndex()) {
            val otherPart = otherPatternParts[i]

            when (thisPart) {
                is TextStepPatternPart -> {
                    if (otherPart !is TextStepPatternPart) {
                        return false
                    }

                    if (thisPart.text != otherPart.text) {
                        return false
                    }
                }
                is ParamStepPatternPart -> {
                    if (otherPart !is ParamStepPatternPart) {
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun toString() = buildString { toString(this, 0) }

    fun toString(destination: StringBuilder,
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

    @Suppress("unused")
    fun toDebugTree() = buildString { toDebugTree(this, 0) }

    @Suppress("MemberVisibilityCanBePrivate")
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
