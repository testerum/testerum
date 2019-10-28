package com.testerum.file_service.util

import com.testerum.model.arg.Arg
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.StepPatternPart

fun TestModel.isChangedRequiringSave(existingTest: TestModel?): Boolean {
    if (existingTest == null) {
        // create test
        return true
    }

    if (path != existingTest.path) {
        // move test to a different directory
        return true
    }

    if (name != existingTest.name) {
        return true
    }

    if (properties != existingTest.properties) {
        return true
    }

    if (description != existingTest.description) {
        return true
    }

    if (tags.isChangedRequiringSave(existingTest.tags)) {
        return true
    }

    if (scenarios.isChangedRequiringSave(existingTest.scenarios)) {
        return true
    }

    if (stepCalls.isChangedRequiringSave(existingTest.stepCalls)) {
        return true
    }

    return false
}

fun ComposedStepDef.isChangedRequiringSave(existingComposedStepDef: ComposedStepDef?): Boolean {
    if (existingComposedStepDef == null) {
        // create composed step
        return true
    }

    if (path != existingComposedStepDef.path) {
        // move composed step to a different directory
        return true
    }

    if (phase != existingComposedStepDef.phase) {
        return true
    }

    if (stepPattern.isChangedRequiringSave(existingComposedStepDef.stepPattern)) {
        return true
    }

    if (description != existingComposedStepDef.description) {
        return true
    }

    if (tags.isChangedRequiringSave(existingComposedStepDef.tags)) {
        return true
    }

    if (stepCalls.isChangedRequiringSave(existingComposedStepDef.stepCalls)) {
        return true
    }

    return false
}

@JvmName("isChangedRequiringSaveTags")
private fun List<String>.isChangedRequiringSave(existingTags: List<String>): Boolean {
    if (this.size != existingTags.size) {
        return true
    }

    for ((i, tag) in this.withIndex()) {
        val existingTag = existingTags[i]

        if (tag != existingTag) {
            return true
        }
    }

    return false
}

@JvmName("isChangedRequiringSaveScenarios")
private fun List<Scenario>.isChangedRequiringSave(existingScenarios: List<Scenario>): Boolean {
    if (this.size != existingScenarios.size) {
        return true
    }

    for ((i, scenario) in this.withIndex()) {
        val existingScenario = existingScenarios[i]

        if (scenario != existingScenario) {
            return true
        }
    }

    return false
}

@JvmName("isChangedRequiringSaveStepCalls")
private fun List<StepCall>.isChangedRequiringSave(existingStepCalls: List<StepCall>): Boolean {
    if (this.size != existingStepCalls.size) {
        return true
    }

    for ((i, stepCall) in this.withIndex()) {
        val existingStepCall = existingStepCalls[i]

        if (stepCall.isChangedRequiringSave(existingStepCall)) {
            return true
        }
    }

    return false
}

private fun StepCall.isChangedRequiringSave(existingStepCall: StepCall): Boolean {
    val stepDef = this.stepDef
    val existingStepDef = existingStepCall.stepDef

    if (stepDef.phase != existingStepDef.phase) {
        return true
    }

    if (stepDef.stepPattern.isChangedRequiringSave(existingStepDef.stepPattern)) {
        return true
    }

    if (args.isChangedRequiringSave(existingStepCall.args)) {
        return true
    }

    return false
}

@JvmName("isChangedRequiringSaveStepArgs")
private fun List<Arg>.isChangedRequiringSave(existingArgs: List<Arg>): Boolean {
    if (this.size != existingArgs.size) {
        return true
    }

    for ((i, arg) in this.withIndex()) {
        val existingArg = existingArgs[i]

        if (arg.isChangedRequiringSave(existingArg)) {
            return true
        }
    }

    return false
}

private fun Arg.isChangedRequiringSave(existingArg: Arg): Boolean {
    if (path != existingArg.path) {
        return true
    }

    if (path == null) {
        // inline arg
        if (this.content != existingArg.content) {
            return true
        }
    }

    return false
}

private fun StepPattern.isChangedRequiringSave(existingStepPattern: StepPattern): Boolean {
    return patternParts.isChangedRequiringSave(existingStepPattern.patternParts)
}

@JvmName("isChangedRequiringSaveStepPatternParts")
private fun List<StepPatternPart>.isChangedRequiringSave(existingParts: List<StepPatternPart>): Boolean {
    if (this.size != existingParts.size) {
        return true
    }

    for ((i, part) in this.withIndex()) {
        val existingPart = existingParts[i]

        if (part != existingPart) {
            return true
        }
    }

    return false
}
