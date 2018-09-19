package com.testerum.file_service.caches.resolved.resolvers

import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepDef
import com.testerum.model.util.StepHashUtil
import java.nio.file.Path as JavaPath

class StepsResolver(private val argsResolver: ArgsResolver) {

    fun resolve(basicStepsMap: Map<String, BasicStepDef>,
                unresolvedComposedStepsMap: Map<String, ComposedStepDef>,
                resourcesDir: JavaPath): MutableMap<String, StepDef> {
        val stepsToResolve   = HashMap<String, ComposedStepDef>(unresolvedComposedStepsMap)
        val stepsInResolving = LinkedHashMap<String, StepDef>()
        val resolvedSteps    = HashMap<String, StepDef>(basicStepsMap)

        for ((hashOfStepToResolve, stepToResolve) in unresolvedComposedStepsMap) {
            resolve(stepToResolve, hashOfStepToResolve, stepsToResolve, stepsInResolving, resolvedSteps, resourcesDir)
        }

        return resolvedSteps
    }

    private fun resolve(stepToResolve: StepDef,
                        hashOfStepToResolve: String,
                        stepsToResolve: MutableMap<String, ComposedStepDef>,
                        stepsInResolving: MutableMap<String, StepDef>,
                        resolvedSteps: MutableMap<String, StepDef>,
                        resourcesDir: JavaPath) {
        if (stepsInResolving.containsKey(hashOfStepToResolve)) {
            throw RuntimeException("detected cycle while resolving steps: ${stepsInResolving.values.joinToString(prefix = "[", postfix = "]", separator = "], [")}")
        }

        stepsInResolving[hashOfStepToResolve] = stepToResolve

        // already resolved
        val alreadyResolvedStep: StepDef? = resolvedSteps[hashOfStepToResolve]
        if (alreadyResolvedStep != null) {
            stepsToResolve.remove(hashOfStepToResolve)
            stepsInResolving.remove(hashOfStepToResolve)

            return
        }

        // cannot resolve
        val notYetResolvedStepDef = stepsToResolve[hashOfStepToResolve]
        if (notYetResolvedStepDef == null) {
            // cannot resolve (e.g. UndefinedStep)
            resolvedSteps[hashOfStepToResolve] = stepToResolve
            stepsToResolve.remove(hashOfStepToResolve)
            stepsInResolving.remove(hashOfStepToResolve)

            return
        }

        // resolve recursively all children
        for (stepCall in notYetResolvedStepDef.stepCalls) {
            val stepDef = stepCall.stepDef
            val hash = StepHashUtil.calculateStepHash(stepDef)

            resolve(stepDef, hash, stepsToResolve, stepsInResolving, resolvedSteps, resourcesDir)
        }

        // all children are resolved, we need to attach them
        val resolvedCalls = notYetResolvedStepDef.stepCalls.map { stepCall ->
            val stepDef: StepDef = stepCall.stepDef
            val hash = StepHashUtil.calculateStepHash(stepDef)

            var resolvedStepDef: StepDef? = resolvedSteps[hash]
            if (resolvedStepDef == null) {
                // could not resolve
                resolvedStepDef = stepDef
            }

            stepCall.copy(
                    stepDef = resolvedStepDef,
                    args = argsResolver.resolveArgs(stepCall.args, resolvedStepDef, resourcesDir)
            )
        }

        resolvedSteps[hashOfStepToResolve] = notYetResolvedStepDef.copy(stepCalls = resolvedCalls)
        stepsToResolve.remove(hashOfStepToResolve)
        stepsInResolving.remove(hashOfStepToResolve)
    }

}
