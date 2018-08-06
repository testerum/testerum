package com.testerum.model.util

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.step.StepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart

object StepHashUtil {

    fun calculateStepHash(stepDef: StepDef): String {
        return calculateStepHash(stepDef.phase, stepDef.stepPattern)
    }

    fun calculateStepHash(stepPhase: StepPhaseEnum, stepPattern: StepPattern) = buildString {
        append(stepPhase.toString())
        append(" ")

        for (patternPart in stepPattern.patternParts) {
            when(patternPart) {
                is TextStepPatternPart -> {
                    append(patternPart.text)
                }
                is ParamStepPatternPart -> {
                    append("<<>>")
                }
            }
        }
    }

}