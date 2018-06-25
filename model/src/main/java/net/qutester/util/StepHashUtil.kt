package net.qutester.util

import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.step.StepDef
import net.qutester.model.text.StepPattern
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.text.parts.TextStepPatternPart

object StepHashUtil {

    fun calculateStepHash(stepDef: StepDef): String {
        return calculateStepHash(stepDef.phase, stepDef.stepPattern)
    }

    fun calculateStepHash(stepPhase: StepPhaseEnum, stepPattern: StepPattern) = buildString {
        append(stepPhase.toString())
        append(" ")

        for (patternPart in stepPattern.patternParts) {
            when(patternPart) {
                is TextStepPatternPart  -> {
                    append(patternPart.text)
                }
                is ParamStepPatternPart -> {
                    append("<<>>")
                }
            }
        }
    }
}