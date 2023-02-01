package com.testerum.file_service.caches.resolved.resolvers

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.manual.ManualStepCall
import com.testerum.model.manual.ManualTest
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel
import java.nio.file.Path as JavaPath

class TestResolver(private val argsResolver: ArgsResolver) {

    fun resolveStepsDefs(getStepsCache: () -> StepsCache,
                         test: TestModel,
                         resourcesDir: JavaPath): TestModel {
        val resolvedStepCalls = resolveStepCalls(getStepsCache, test.stepCalls, resourcesDir)

        return test.copy(
                stepCalls = resolvedStepCalls
        )
    }

    fun resolveAfterHooksStepsDefs(getStepsCache: () -> StepsCache,
                                   test: TestModel,
                                   resourcesDir: JavaPath): TestModel {
        val resolvedAfterHooks = resolveStepCalls(getStepsCache, test.afterHooks, resourcesDir)

        return test.copy(
                afterHooks = resolvedAfterHooks
        )
    }

    private fun resolveStepCalls(getStepsCache: () -> StepsCache, stepCalls: List<StepCall>, resourcesDir: java.nio.file.Path): List<StepCall> {
        val stepsCache = getStepsCache()

        val resolvedStepCalls = mutableListOf<StepCall>()

        for (stepCall in stepCalls) {
            val stepDef = stepCall.stepDef

            val resolvedStepDef = stepsCache.getStepDefByPhaseAndPattern(stepPhase = stepDef.phase, stepPattern = stepDef.stepPattern)

            resolvedStepCalls += stepCall.copy(
                stepDef = resolvedStepDef,
                args = argsResolver.resolveArgs(stepCall.args, resolvedStepDef, resourcesDir)
            )
        }

        return resolvedStepCalls
    }

    fun resolveManualStepDefs(getStepsCache: () -> StepsCache,
                              manualTest: ManualTest,
                              resourcesDir: JavaPath): ManualTest {
        val stepsCache = getStepsCache()

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
