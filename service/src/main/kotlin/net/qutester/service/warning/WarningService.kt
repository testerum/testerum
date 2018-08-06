package net.qutester.service.warning

import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.warning.Warning

class WarningService {

    fun testWithWarnings(test: TestModel,
                         keepExistingWarnings: Boolean): TestModel {
        // fill-in own warnings
        val warnings: MutableList<Warning> = if (keepExistingWarnings) {
            test.warnings.toMutableList()
        } else {
            mutableListOf()
        }

        addTestWarnings(warnings, test)

        // check children
        val stepCallsWithWarnings = mutableListOf<StepCall>()

        for (stepCall in test.stepCalls) {
            stepCallsWithWarnings.add(
                    stepCallWithWarnings(stepCall, keepExistingWarnings)
            )
        }

        // return copy with warnings filled-in
        return test.copy(
                stepCalls = stepCallsWithWarnings,
                warnings = warnings
        )
    }

    private fun stepCallWithWarnings(stepCall: StepCall,
                                     keepExistingWarnings: Boolean): StepCall {
        // fill-in own warnings
        val warnings: MutableList<Warning> = if (keepExistingWarnings) {
            stepCall.warnings.toMutableList()
        } else {
            mutableListOf()
        }

        addStepCallWarnings(warnings, stepCall)

        // check children
        val stepDef: StepDef = stepCall.stepDef
        if (stepDef is ComposedStepDef) {
            val stepDefWithWarnings = composedStepWithWarnings(stepDef, keepExistingWarnings)

            return stepCall.copy(
                    stepDef = stepDefWithWarnings,
                    warnings = warnings
            )
        }

        // return copy with warnings filled-in
        return stepCall.copy(
                warnings = warnings
        )
    }

    fun composedStepWithWarnings(stepDef: ComposedStepDef,
                                 keepExistingWarnings: Boolean): ComposedStepDef {
        // fill-in own warnings
        val warnings: MutableList<Warning> = if (keepExistingWarnings) {
            stepDef.warnings.toMutableList()
        } else {
            mutableListOf()
        }

        addComposedStepDefWarnings(warnings, stepDef)

        // check children
        val childStepCallsWithWarnings = mutableListOf<StepCall>()

        for (childStepCall in stepDef.stepCalls) {
            childStepCallsWithWarnings.add(
                    stepCallWithWarnings(childStepCall, keepExistingWarnings)
            )
        }

        // return copy with warnings filled-in
        return stepDef.copy(
                stepCalls = childStepCallsWithWarnings,
                warnings = warnings
        )
    }

    private fun addTestWarnings(warnings: MutableList<Warning>,
                                test: TestModel) {
        // missing children steps
        if (test.stepCalls.isEmpty()) {
            warnings += Warning.TEST_WITHOUT_STEP_CALLS
        }
    }

    private fun addStepCallWarnings(warnings: MutableList<Warning>,
                                    stepCall: StepCall) {
        val stepDef: StepDef = stepCall.stepDef

        if (stepDef is UndefinedStepDef) {
            warnings += Warning.UNDEFINED_STEP_CALL
        }
    }

    private fun addComposedStepDefWarnings(warnings: MutableList<Warning>,
                                           stepDef: ComposedStepDef) {
        // missing children steps
        if (stepDef.stepCalls.isEmpty()) {
            warnings += Warning.COMPOSED_STEP_WITHOUT_STEP_CALLS
        }
    }

}