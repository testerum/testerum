package net.qutester.service.step

import net.qutester.exception.ServerStateChangedException
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.operation.UpdateComposedStepDef
import net.qutester.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import net.qutester.model.test.TestModel
import net.qutester.model.text.StepPattern
import net.qutester.service.tests.TestsService
import net.qutester.service.step.util.hasTheSameStepPattern
import net.qutester.service.step.util.isCallingStepPattern
import net.qutester.service.step.util.isOtherStepWithTheSameStepPattern
import net.qutester.service.step.util.isStepPatternChangeCompatible
import net.qutester.service.tests.util.isTestUsingStepPattern

class StepUpdateCompatibilityService (var stepService: StepService,
                                      var testsService: TestsService) {

    fun checkUpdateCompatibility(updateComposedStepDef: UpdateComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        val oldStepPattern = stepService.getComposedStepByPath(updateComposedStepDef.oldPath)?.stepPattern ?: throw ServerStateChangedException()
        val newStepPattern = updateComposedStepDef.composedStepDef.stepPattern

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
        val pathsForTransitiveAffectedSteps: List<Path> = findStepsThatUsesStepPatternAsTransitiveChild(oldStepPattern) - pathsForDirectAffectedSteps;


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
        val composedSteps = stepService.getComposedSteps()

        for (composedStep in composedSteps) {
            if(composedStep.isCallingStepPattern(searchedStepPattern)) {
                result.add(composedStep.path)
            }
        }

        return result
    }

    fun findStepsThatUsesStepPatternAsDirectChild(searchedStepPattern: StepPattern): List<ComposedStepDef> {
        val result: MutableSet<ComposedStepDef> = mutableSetOf()

        val composedSteps = stepService.getComposedSteps()
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
        val result: MutableList<TestModel> = mutableListOf();

        val allTests = testsService.getAllTests()
        for (test in allTests) {
            if (test.isTestUsingStepPattern(searchedStepPattern)) {
                result.add(test)
            }
        }

        return result
    }

    fun isOtherStepWithTheSameStepPattern(oldStepPattern: StepPattern, newStepPattern: StepPattern): Boolean {
        val allSteps = stepService.getAllSteps()
        val allStepsPatterns = allSteps.map { it.stepPattern }

        return isOtherStepWithTheSameStepPattern(allStepsPatterns, oldStepPattern, newStepPattern)
    }
}