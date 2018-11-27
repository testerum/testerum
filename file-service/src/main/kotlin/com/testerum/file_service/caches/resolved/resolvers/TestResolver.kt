package com.testerum.file_service.caches.resolved.resolvers

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.manual.ManualStepCall
import com.testerum.model.manual.ManualTest
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel
import java.nio.file.Path as JavaPath

class TestResolver(private val stepsCache: StepsCache,
                   private val argsResolver: ArgsResolver) {

    fun resolveStepsDefs(test: TestModel, resourcesDir: JavaPath): TestModel {
        val resolvedStepCalls = mutableListOf<StepCall>()

        for (stepCall in test.stepCalls) {
            val stepDef = stepCall.stepDef

            val resolvedStepDef = stepsCache.getStepDefByPhaseAndPattern(stepPhase = stepDef.phase, stepPattern = stepDef.stepPattern)

            resolvedStepCalls += stepCall.copy(
                    stepDef = resolvedStepDef,
                    args = argsResolver.resolveArgs(stepCall.args, resolvedStepDef, resourcesDir)
            )
        }

        return test.copy(
                stepCalls = resolvedStepCalls
        )
    }

    fun resolveManualStepDefs(manualTest: ManualTest, resourcesDir: JavaPath): ManualTest {
        val resolvedManualStepCalls = mutableListOf<ManualStepCall>()

        for (manualStepCall in manualTest.stepCalls) {
            val stepCall = manualStepCall.stepCall
            val stepDef = stepCall.stepDef

            val resolvedStepDef = stepsCache.getStepDefByPhaseAndPattern(stepPhase = stepDef.phase, stepPattern = stepDef.stepPattern)

            val resolvedStepCall = stepCall.copy(
                    stepDef = resolvedStepDef,
                    args = argsResolver.resolveArgs(stepCall.args, resolvedStepDef, resourcesDir)
            )

            resolvedManualStepCalls += manualStepCall.copy(
                    stepCall = resolvedStepCall
            )
        }

        return manualTest.copy(
                stepCalls = resolvedManualStepCalls
        )

    }
}
