package com.testerum.web_backend.services.steps

import com.testerum.file_service.util.hasTheSameStepPattern
import com.testerum.file_service.util.isCallingStepPattern
import com.testerum.file_service.util.isStepPatternChangeCompatible
import com.testerum.model.exception.ServerStateChangedException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.step.operation.response.CheckComposedStepDefUsageResponse
import com.testerum.model.test.TestModel
import com.testerum.model.text.StepPattern
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.util.isOtherStepWithTheSameStepPatternAsTheNew
import com.testerum.web_backend.util.isTestUsingStepPattern

class ComposedStepUpdateCompatibilityFrontendService(private val webProjectManager: WebProjectManager) {

    private fun stepsCache() = webProjectManager.getProjectServices().getStepsCache()

    fun checkUpdateCompatibility(composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        val oldPath = composedStepDef.oldPath
                ?: throw IllegalArgumentException("this service is only supported for existing steps (not for creating steps)")

        val oldStep = stepsCache().getComposedStepAtPath(oldPath)
                ?: throw ServerStateChangedException("the old step no longer exists")

        // ~~~~~~~~~~ check if the new step pattern is compatible with the old one ~~~~~~~~~~
        val oldStepPattern = oldStep.stepPattern
        val newStepPattern = composedStepDef.stepPattern

        val allSteps = stepsCache().getAllSteps()

        if (oldStepPattern.hasTheSameStepPattern(newStepPattern)) {
            return CheckComposedStepDefUpdateCompatibilityResponse(isCompatible = true)
        }

        if (isOtherStepWithTheSameStepPatternAsTheNew(oldStepPattern, newStepPattern, allSteps)) {
            // todo: @Ionut: I don't understand: why would this be compatible?
            return CheckComposedStepDefUpdateCompatibilityResponse(isCompatible = true, isUniqueStepPattern = false)
        }

        if (oldStepPattern.isStepPatternChangeCompatible(newStepPattern)) {
            return CheckComposedStepDefUpdateCompatibilityResponse(isCompatible = true, isUniqueStepPattern = true)
        }

        // ~~~~~~~~~~ check if this update affects other composed steps and/or tests ~~~~~~~~~~
        // todo: move this checks to a separate endpoint (we want to show these before editing the step, not just before save, after I already wasted time editing)
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

    private fun findTestsThatUsesStepPatternAsChild(searchedStepPattern: StepPattern): List<TestModel> {
        val result: MutableList<TestModel> = mutableListOf()

        val allTests = webProjectManager.getProjectServices().getTestsCache().getAllTests()
        for (test in allTests) {
            if (test.isTestUsingStepPattern(searchedStepPattern)) {
                result.add(test)
            }
        }

        return result
    }

    private fun findStepsThatUsesStepPatternAsDirectChild(searchedStepPattern: StepPattern): List<ComposedStepDef> {
        val result: MutableSet<ComposedStepDef> = mutableSetOf()

        val composedSteps = stepsCache().getComposedSteps()
        for (composedStep in composedSteps) {
            for (stepCall in composedStep.stepCalls) {
                if (stepCall.stepDef.stepPattern == searchedStepPattern) {
                    result.add(composedStep)
                }
            }
        }
        return result.toList()
    }

    private fun findStepsThatUsesStepPatternAsTransitiveChild(searchedStepPattern: StepPattern): List<Path> {
        val result: MutableList<Path> = mutableListOf()
        val composedSteps = stepsCache().getComposedSteps()

        for (composedStep in composedSteps) {
            if(composedStep.isCallingStepPattern(searchedStepPattern)) {
                result.add(composedStep.path)
            }
        }

        return result
    }

    fun checkUsage(composedStepDef: ComposedStepDef): CheckComposedStepDefUsageResponse {
        val stepPattern = composedStepDef.stepPattern
        val pathsForParentTests: List<Path> = findTestsThatUsesStepPatternAsChild(stepPattern).map { it.path }
        val pathsForDirectParentSteps: List<Path> = findStepsThatUsesStepPatternAsDirectChild(stepPattern).map { it.path }
        val pathsForTransitiveParentSteps: List<Path> = findStepsThatUsesStepPatternAsTransitiveChild(stepPattern) - pathsForDirectParentSteps

        val isStepUsed = pathsForParentTests.isNotEmpty() || pathsForDirectParentSteps.isNotEmpty() || pathsForTransitiveParentSteps.isNotEmpty()

        return CheckComposedStepDefUsageResponse(
            isUsed = isStepUsed,
            pathsForParentTests = pathsForParentTests,
            pathsForDirectParentSteps = pathsForDirectParentSteps,
            pathsForTransitiveParentSteps = pathsForTransitiveParentSteps
        )
    }
}
