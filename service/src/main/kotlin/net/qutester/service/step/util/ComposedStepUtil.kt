package net.qutester.service.step.util

import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step.StepDef
import net.qutester.model.text.StepPattern
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.text.parts.TextStepPatternPart

fun getStepWithTheSameStepDef(stepDef: StepDef, existingSteps: List<StepDef>): StepDef? {
    for (existingStep in existingSteps) {
        if (stepDef.stepPattern.hasTheSameStepPattern(existingStep.stepPattern)) {
            return existingStep
        }
    }
    return null;
}

fun StepPattern.hasTheSameStepPattern(otherStepPattern: StepPattern): Boolean {
    val leftPatternParts = this.patternParts
    val rightPatternParts = otherStepPattern.patternParts

    if (leftPatternParts.size != rightPatternParts.size) {
        return false
    }

    val leftTextPatternParts: List<TextStepPatternPart> = leftPatternParts.filter { it is TextStepPatternPart }.map { it as TextStepPatternPart }
    val rightTextPatternParts: List<TextStepPatternPart> = rightPatternParts.filter { it is TextStepPatternPart }.map { it as TextStepPatternPart }

    if (leftTextPatternParts != rightTextPatternParts) {
        return false
    }

    val leftParamPatternPart: List<ParamStepPatternPart> = leftPatternParts.filter { it is ParamStepPatternPart }.map { it as ParamStepPatternPart }
    val rightParamPatternPart: List<ParamStepPatternPart> = rightPatternParts.filter { it is ParamStepPatternPart }.map { it as ParamStepPatternPart }

    if (leftParamPatternPart != rightParamPatternPart) {
        return false
    }

    return true
}

fun isOtherStepWithTheSameStepPattern(allStepPatterns: List<StepPattern>, oldStepPattern: StepPattern, newStepPattern: StepPattern): Boolean {
    val otherStepPatterns: List<StepPattern> = removeStepPatternFromList(allStepPatterns, oldStepPattern)
    return isOtherStepWithTheSameStepPattern(otherStepPatterns, newStepPattern)
}

private fun removeStepPatternFromList(allStepPatterns: List<StepPattern>, stepPatternToRemove: StepPattern): List<StepPattern> {
    return allStepPatterns.filter { it != stepPatternToRemove }
}

private fun isOtherStepWithTheSameStepPattern(allStepPatterns: List<StepPattern>, searchedStepPattern: StepPattern): Boolean {

    for (stepPattern in allStepPatterns) {

        val areStepsConflicting: Boolean = stepPattern.areStepPatternsConflicting(searchedStepPattern)
        if (areStepsConflicting) {
            return true;
        }
    }

    return false
}

fun StepPattern.areStepPatternsConflicting(otherStepPattern: StepPattern): Boolean {
    if (this.patternParts.size != otherStepPattern.patternParts.size) {
        return false
    }

    for ((index, patternPart) in this.patternParts.withIndex()) {
        val searchPatternPart = otherStepPattern.patternParts[index]
        if (patternPart is TextStepPatternPart && searchPatternPart is TextStepPatternPart) {
            if (patternPart != searchPatternPart) {
                return false
            }
            continue
        }

        if (patternPart is ParamStepPatternPart && searchPatternPart is ParamStepPatternPart) {
            continue
        }

        return false
    }
    return true;
}

fun StepPattern.isStepPatternChangeCompatible(otherStepPattern: StepPattern): Boolean {
    var currentParamIndex = 0

    if (this.getParamStepPattern().size != otherStepPattern.getParamStepPattern().size) {
        return false
    }
    
    for (patternPart in this.patternParts) {
        if (patternPart is ParamStepPatternPart) {
            val otherParamPatternPart = otherStepPattern.getParamPatternPartByIndex(currentParamIndex)
            if (otherParamPatternPart == null) {
                return false
            }

            if (patternPart.type != otherParamPatternPart.type) {
                return false
            }

            currentParamIndex++
        }
    }

    return true
}

private fun StepPattern.getParamPatternPartByIndex(searchedParamPatternIndex: Int): ParamStepPatternPart? {
    var currentParamPattentPartIndex = 0
    for (patternPart in patternParts) {
        if (patternPart is ParamStepPatternPart) {
            if (currentParamPattentPartIndex == searchedParamPatternIndex) {
                return patternPart
            }
            currentParamPattentPartIndex ++
        }
    }

    return null
}

fun ComposedStepDef.isCallingStepPattern(searchedStepPattern: StepPattern): Boolean {
    for (stepCall in stepCalls) {
        val stepDef = stepCall.stepDef
        if(stepDef.stepPattern == searchedStepPattern) {
            return true
        }

        if (stepDef is ComposedStepDef) {
            val isCallingStepPattern = stepDef.isCallingStepPattern(searchedStepPattern)
            if (isCallingStepPattern) {
                return true
            }
        }
    }

    return false
}
