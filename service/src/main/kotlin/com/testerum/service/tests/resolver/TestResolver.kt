package com.testerum.service.tests.resolver

import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.service.step.StepService

class TestResolver(private val stepService: StepService) {

    fun resolveSteps(testModel: TestModel, throwExceptionOnNotFound: Boolean = true): TestModel {

        val resolvedStepCalls: MutableList<StepCall> = mutableListOf()
        for (stepCall in testModel.stepCalls) {

            val stepDef = stepCall.stepDef
            val resolvedStepDef = stepService.getStepDefByPhaseAndPattern(stepPhase = stepDef.phase, stepPattern = stepDef.stepPattern)

            if (throwExceptionOnNotFound && resolvedStepDef is UndefinedStepDef) {
                throw RuntimeException("The step [${stepDef.getText()}] has bean deleted")
            }

            resolvedStepCalls += StepCall(
                    stepCall.id,
                    resolvedStepDef,
                    stepCall.args
            )
        }

        return TestModel(
                path = testModel.path,
                properties = testModel.properties,
                text = testModel.text,
                description = testModel.description,
                tags = testModel.tags,
                stepCalls = resolvedStepCalls
        )
    }

}
