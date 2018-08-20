package com.testerum.service.step

import com.testerum.model.exception.ServerStateChangedException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.service.step.util.hasTheSameStepPattern
import com.testerum.service.step.util.isCallingStepPattern
import com.testerum.service.step.util.isOtherStepWithTheSameStepPattern
import com.testerum.service.step.util.isStepPatternChangeCompatible
import com.testerum.service.tests.TestsService
import com.testerum.service.tests.util.isTestUsingStepPattern

class StepUpdateCompatibilityService (var stepCache: StepCache,
                                      var testsService: TestsService) {

    fun checkUpdateCompatibility(composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        val oldPath = composedStepDef.oldPath
                ?: throw IllegalArgumentException("this service is only supported for existing steps (not for creating steps)")

        val oldStepPattern = stepCache.getComposedStepAtPath(oldPath)?.stepPattern
                ?: throw ServerStateChangedException()
        val newStepPattern = composedStepDef.stepPattern

        if (oldStepPattern.hasTheSameStepPattern(newStepPattern)) {
            return CheckComposedStepDefUpdateCompatibilityResponse(isCompatible = true)
        }

        if (isOtherStepWithTheSameStepPattern(oldStepPattern, newStepPattern)) {
            return CheckComposedStepDefUpdateCompatibilityResponse(isCompatible = true, isUniqueStepPattern = false)
        }

        if (oldStepPattern.isStepPatternChangeCompatible(newStepPattern)) {
            return CheckComposedStepDefUpdateCompatibilityResponse(isCompatible = true, isUniqueStepPattern = true)
        }

        val pathsForAffectedTests: List<Path> = findTestsThatUsesStepPatternAsChild(oldStepPattern).map { it.path }
        val pathsForDirectAffectedSteps: List<Path> = findStepsThatUsesStepPatternAsDirectChild(oldStepPattern).map { it.path }
        val pathsForTransitiveAffectedSteps: List<Path> = findStepsThatUsesStepPatternAsTransitiveChild(oldStepPattern) - pathsForDirectAffectedSteps

        return CheckComposedStepDefUpdateCompatibilityResponse(
                isCompatible = false,
                isUniqueStepPattern = true,
                pathsForAffectedTests = pathsForAffectedTests,
                pathsForDirectAffectedSteps = pathsForDirectAffectedSteps,
                pathsForTransitiveAffectedSteps = pathsForTransitiveAffectedSteps
        )
    }

    private fun findStepsThatUsesStepPatternAsTransitiveChild(searchedStepPattern: StepPattern): List<Path> {
        val result: MutableList<Path> = mutableListOf()
        val composedSteps = stepCache.getComposedSteps()

        for (composedStep in composedSteps) {
            if(composedStep.isCallingStepPattern(searchedStepPattern)) {
                result.add(composedStep.path)
            }
        }

        return result
    }

    fun findStepsThatUsesStepPatternAsDirectChild(searchedStepPattern: StepPattern): List<ComposedStepDef> {
        val result: MutableSet<ComposedStepDef> = mutableSetOf()

        val composedSteps = stepCache.getComposedSteps()
        for (composedStep in composedSteps) {
            for (stepCall in composedStep.stepCalls) {
                if (stepCall.stepDef.stepPattern == searchedStepPattern) {
                    result.add(composedStep)
                }
            }
        }
        return result.toList()
    }

    fun findTestsThatUsesStepPatternAsChild(searchedStepPattern: StepPattern): List<TestModel> {
        val result: MutableList<TestModel> = mutableListOf()

        val allTests = testsService.getAllTests()
        for (test in allTests) {
            if (test.isTestUsingStepPattern(searchedStepPattern)) {
                result.add(test)
            }
        }

        return result
    }

    fun isOtherStepWithTheSameStepPattern(oldStepPattern: StepPattern, newStepPattern: StepPattern): Boolean {
        val allSteps = stepCache.getAllSteps()
        val allStepsPatterns = allSteps.map { it.stepPattern }

        return isOtherStepWithTheSameStepPattern(allStepsPatterns, oldStepPattern, newStepPattern)
    }
}

