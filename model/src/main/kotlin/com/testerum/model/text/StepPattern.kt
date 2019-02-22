package com.testerum.model.text

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart

data class StepPattern @JsonCreator constructor(
        @JsonProperty("patternParts") val patternParts: List<StepPatternPart>
) {

    fun appendPart(partToAppend: StepPatternPart): StepPattern {
        val lastPatternPart = patternParts.lastOrNull()

        val shouldMergeIntoTheLastPart = lastPatternPart != null
                && lastPatternPart is TextStepPatternPart
                && partToAppend is TextStepPatternPart

        val newPatternParts = ArrayList<StepPatternPart>()
        if (shouldMergeIntoTheLastPart) {
            for ((i, patternPart) in patternParts.withIndex()) {
                val isLastPart = (i == (patternParts.size - 1))

                if (isLastPart) {
                    newPatternParts.add(
                            TextStepPatternPart(
                                    (patternPart as TextStepPatternPart).text + (partToAppend as TextStepPatternPart).text
                            )
                    )
                } else {
                    newPatternParts.add(patternPart)
                }
            }
        } else {
            newPatternParts.addAll(this.patternParts)
            newPatternParts.add(partToAppend)
        }

        return this.copy(patternParts = newPatternParts)
    }

    @JsonIgnore
    fun getAsText(): String {
        var result = ""
        var isFirstPart = true
        for (patternPart in patternParts) {

            if (!isFirstPart) {
                result += " "
            }

            if (patternPart is TextStepPatternPart) {
                result += patternPart.text
            }

            if (patternPart is ParamStepPatternPart) {
                result += "[" + patternPart.name + "]"
            }

            isFirstPart = false
        }
        return result
    }

    @JsonIgnore
    fun getParamStepPattern(): List<ParamStepPatternPart> {
        return patternParts
                .filter { stepPatternPart -> stepPatternPart is ParamStepPatternPart }
                .map { item -> item as ParamStepPatternPart }
    }

    override fun toString() = toDebugString("[", "]")

    fun toDebugString(varPrefix: String, varSuffix: String) = buildString {
        var argIndex = 0
        for (patternPart in patternParts) {
            when (patternPart) {
                is TextStepPatternPart -> append(patternPart.text)
                is ParamStepPatternPart -> {
                    append(varPrefix)
                    append(patternPart.name)
                    append(varSuffix)

                    argIndex++
                }
            }
        }
    }

}
