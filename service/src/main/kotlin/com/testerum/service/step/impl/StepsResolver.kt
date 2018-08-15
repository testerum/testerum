package com.testerum.service.step.impl

import com.testerum.common_kotlin.emptyToNull
import com.testerum.model.arg.Arg
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.util.StepHashUtil
import com.testerum.service.mapper.file_arg_transformer.FileArgTransformer

class StepsResolver(private val unresolvedComposedStepsMap: Map<String, ComposedStepDef>,
                    basicStepsMap: Map<String, BasicStepDef>) {

    private val stepsToResolve   = HashMap<String, ComposedStepDef>(unresolvedComposedStepsMap)
    private val stepsInResolving = LinkedHashMap<String, StepDef>()
    private val resolvedSteps    = HashMap<String, StepDef>(basicStepsMap)

    fun resolve(): MutableMap<String, StepDef> {
        for ((hashOfStepToResolve, stepToResolve) in unresolvedComposedStepsMap) {
            resolve(stepToResolve, hashOfStepToResolve)
        }

        return resolvedSteps
    }

    private fun resolve(stepToResolve: StepDef,
                        hashOfStepToResolve: String) {
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

            resolve(stepDef, hash)
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

            // resolve arg types from step def
            val resolvedArgs = mutableListOf<Arg>()

            val paramParts: List<ParamStepPatternPart> = resolvedStepDef.stepPattern.getParamStepPattern()

            for ((i, arg) in stepCall.args.withIndex()) {
                val paramType = paramParts[i].type
                val content = FileArgTransformer.fileFormatToJson(arg.content.orEmpty(), paramType).emptyToNull()

                resolvedArgs.add(
                        arg.copy(
                                type = paramType,
                                content = content
                        )
                )
            }

            stepCall.copy(
                    stepDef = resolvedStepDef,
                    args = resolvedArgs
            )
        }

        resolvedSteps[hashOfStepToResolve] = notYetResolvedStepDef.copy(stepCalls = resolvedCalls)
        stepsToResolve.remove(hashOfStepToResolve)
        stepsInResolving.remove(hashOfStepToResolve)
    }

}
