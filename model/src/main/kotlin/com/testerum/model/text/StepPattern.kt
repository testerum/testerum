package com.testerum.model.text

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart

data class StepPattern  @JsonCreator constructor(
        @JsonProperty("patternParts") val patternParts: List<StepPatternPart>
) {
    @JsonIgnore
    fun getAsText():String {
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
                result += "["+ patternPart.name + "]"
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