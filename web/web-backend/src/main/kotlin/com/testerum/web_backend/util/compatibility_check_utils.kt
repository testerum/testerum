package com.testerum.web_backend.util

import com.testerum.file_service.util.isOtherStepWithTheSameStepPattern
import com.testerum.model.step.StepDef
import com.testerum.model.text.StepPattern

fun isOtherStepWithTheSameStepPatternAsTheNew(oldStepPattern: StepPattern, newStepPattern: StepPattern, allSteps: Collection<StepDef>): Boolean {
    val allStepsPatterns = allSteps.map { it.stepPattern }

    return isOtherStepWithTheSameStepPattern(allStepsPatterns, oldStepPattern, newStepPattern)
}
