package com.testerum.file_service.util

import com.testerum.model.step.ComposedStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart

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
            return true
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
    return true
}

fun StepPattern.isStepPatternChangeCompatible(otherStepPattern: StepPattern): Boolean {
    var currentParamIndex = 0

    if (this.getParamStepPattern().size != otherStepPattern.getParamStepPattern().size) {
        return false
    }
    
    for (patternPart in this.patternParts) {
        if (patternPart is ParamStepPatternPart) {
            val otherParamPatternPart = otherStepPattern.getParamPatternPartByIndex(currentParamIndex)
                    ?: return false

            if (patternPart.typeMeta != otherParamPatternPart.typeMeta) {
                return false
            }

            currentParamIndex++
        }
    }

    return true
}

private fun StepPattern.getParamPatternPartByIndex(searchedParamPatternIndex: Int): ParamStepPatternPart? {
    var currentParamPatternPartIndex = 0

    for (patternPart in patternParts) {
        if (patternPart is ParamStepPatternPart) {
            if (currentParamPatternPartIndex == searchedParamPatternIndex) {
                return patternPart
            }
            currentParamPatternPartIndex ++
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
