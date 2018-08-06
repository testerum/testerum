package com.testerum.service.tests.util

import com.testerum.model.step.ComposedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.service.step.util.hasTheSameStepPattern
import com.testerum.service.step.util.isCallingStepPattern

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

