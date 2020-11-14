package com.testerum.file_service.caches.warnings

import com.testerum.model.arg.Arg
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.warning.Warning
import org.slf4j.LoggerFactory

class WarningService {

    companion object {
        private val LOG = LoggerFactory.getLogger(WarningService::class.java)
    }

    fun testWithWarnings(test: TestModel): TestModel {
        if (test.properties.isManual || test.properties.isDisabled) {
            // some steps may have warnings on them (taken like this from the cache),
            // and we don't want this for manual tests
            return removeWarningsFromTest(test)
        }

        // fill-in own warnings
        val warnings: MutableList<Warning> = mutableListOf()

        addTestWarnings(warnings, test)

        // check children
        val stepCallsWithWarnings = mutableListOf<StepCall>()

        for (stepCall in test.stepCalls) {
            stepCallsWithWarnings.add(
                    stepCallWithWarnings(stepCall)
            )
        }

        // check children
        val afterHooksWithWarnings = mutableListOf<StepCall>()

        for (afterHook in test.afterHooks) {
            afterHooksWithWarnings.add(
                    stepCallWithWarnings(afterHook)
            )
        }

        // return copy with warnings filled-in
        return test.copy(
                stepCalls = stepCallsWithWarnings,
                afterHooks = afterHooksWithWarnings,
                warnings = warnings
        )
    }

    private fun removeWarningsFromTest(test: TestModel): TestModel {
        return test.copy(
                warnings = emptyList(),
                stepCalls = test.stepCalls.map {
                    removeWarningsFromStepCall(it)
                }
        )
    }

    private fun removeWarningsFromStepCall(stepCall: StepCall): StepCall {
        return stepCall.copy(
                warnings = emptyList(),
                stepDef = removeWarningsFromStepDef(stepCall.stepDef),
                args = stepCall.args.map {
                    removeWarningsFromStepCallArg(it)
                }
        )
    }

    private fun removeWarningsFromStepDef(stepDef: StepDef): StepDef {
        return when (stepDef) {
            is UndefinedStepDef -> stepDef // undefined step defs don't have warnings (only step calls do)
            is BasicStepDef -> stepDef.copy(warnings = emptyList())
            is ComposedStepDef -> stepDef.copy(
                    warnings = emptyList(),
                    stepCalls = stepDef.stepCalls.map {
                        removeWarningsFromStepCall(it)
                    }
            )
            else -> {
                LOG.warn("could not remove warnings from step def, because it has an unknown type [${stepDef.javaClass.name}]")

                stepDef
            }
        }
    }

    private fun removeWarningsFromStepCallArg(arg: Arg): Arg {
        return arg.copy(warnings = emptyList())
    }

    private fun stepCallWithWarnings(stepCall: StepCall): StepCall {
        // fill-in own warnings
        val warnings: MutableList<Warning> = mutableListOf()

        addStepCallWarnings(warnings, stepCall)

        // check children
        val stepDef: StepDef = stepCall.stepDef
        if (stepDef is ComposedStepDef) {
            val stepDefWithWarnings = composedStepWithWarnings(stepDef)

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

    fun composedStepWithWarnings(stepDef: ComposedStepDef): ComposedStepDef {
        // fill-in own warnings
        val warnings: MutableList<Warning> = mutableListOf()

        addComposedStepDefWarnings(warnings, stepDef)

        // check children
        val childStepCallsWithWarnings = mutableListOf<StepCall>()

        for (childStepCall in stepDef.stepCalls) {
            childStepCallsWithWarnings.add(
                    stepCallWithWarnings(childStepCall)
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
