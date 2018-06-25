package net.qutester.service.tests.util

import net.qutester.model.step.ComposedStepDef
import net.qutester.model.test.TestModel
import net.qutester.model.text.StepPattern
import net.qutester.service.step.util.hasTheSameStepPattern
import net.qutester.service.step.util.isCallingStepPattern

fun TestModel.isTestUsingStepPattern(searchedStepPattern: StepPattern): Boolean {
    for (stepCall in stepCalls) {

        val stepDef = stepCall.stepDef
        if(stepDef.stepPattern.hasTheSameStepPattern(searchedStepPattern)) { return true }

        if (stepDef is ComposedStepDef) {
            val isCallingStepPattern = stepDef.isCallingStepPattern(searchedStepPattern)
            if (isCallingStepPattern) {
                return true
            }
        }
    }

    return false
}

