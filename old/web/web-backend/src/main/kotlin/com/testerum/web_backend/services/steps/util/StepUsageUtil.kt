package com.testerum.web_backend.util

import com.testerum.file_service.util.hasTheSameStepPattern
import com.testerum.file_service.util.isCallingStepPattern
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern

fun List<StepCall>.isStepPatternUsed(searchedStepPattern: StepPattern): Boolean {
    for (stepCall in this) {
        val stepDef = stepCall.stepDef
        if (stepDef.stepPattern.hasTheSameStepPattern(searchedStepPattern)) {
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

