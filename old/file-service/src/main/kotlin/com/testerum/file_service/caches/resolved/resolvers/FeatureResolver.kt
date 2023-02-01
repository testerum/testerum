package com.testerum.file_service.caches.resolved.resolvers

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.Hooks
import com.testerum.model.step.StepCall
import java.nio.file.Path as JavaPath

class FeatureResolver(private val argsResolver: ArgsResolver) {

    fun resolveHooks(getStepsCache: () -> StepsCache,
                     feature: Feature,
                     resourcesDir: JavaPath): Feature {
        val hooks = feature.hooks
        val resolvedBeforeAllHooks = resolveStepCalls(getStepsCache, hooks.beforeAll, resourcesDir)
        val resolvedBeforeEachHooks = resolveStepCalls(getStepsCache, hooks.beforeEach, resourcesDir)
        val resolvedAfterEachHooks = resolveStepCalls(getStepsCache, hooks.afterEach, resourcesDir)
        val resolvedAfterAllHooks = resolveStepCalls(getStepsCache, hooks.afterAll, resourcesDir)

        return feature.copy(
                hooks = Hooks(
                    beforeAll = resolvedBeforeAllHooks,
                    beforeEach = resolvedBeforeEachHooks,
                    afterEach = resolvedAfterEachHooks,
                    afterAll = resolvedAfterAllHooks
                )
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

}
