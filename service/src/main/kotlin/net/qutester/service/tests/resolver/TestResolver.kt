package net.qutester.service.tests.resolver

import net.qutester.model.arg.Arg
import net.qutester.model.enums.ParamTypeEnum
import net.qutester.model.step.StepCall
import net.qutester.model.step.StepDef
import net.qutester.model.step.UndefinedStepDef
import net.qutester.model.test.TestModel
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.service.step.StepService

class TestResolver(private val stepService: StepService) {

    fun resolveComposedSteps(testModel: TestModel, throwExceptionOnNotFound: Boolean = true): TestModel {

        val resolvedStepCalls: MutableList<StepCall> = mutableListOf()
        for (stepCall in testModel.stepCalls) {

            val stepDef = stepCall.stepDef
            val resolvedStepDef = stepService.getStepDefByPhaseAndPattern(stepPhase = stepDef.phase, stepPattern = stepDef.stepPattern)

            if (throwExceptionOnNotFound && resolvedStepDef is UndefinedStepDef) {
                throw RuntimeException("The step [${stepDef.getText()}] has bean deleted");
            }

            resolvedStepCalls += StepCall(
                    stepCall.id,
                    resolvedStepDef,
                    resolveArguments(stepCall.args, resolvedStepDef)
            )
        }

        return TestModel(
                testModel.path,
                testModel.text,
                testModel.description,
                resolvedStepCalls
        )
    }

    private fun resolveArguments(args: List<Arg>,
                                 resolvedStepDef: StepDef): List<Arg> {
        val result = mutableListOf<Arg>()

        val paramParts: List<ParamStepPatternPart> = resolvedStepDef.stepPattern.getParamStepPattern()

        for ((i: Int, arg: Arg) in args.withIndex()) {
            val paramPart: ParamStepPatternPart = paramParts[i]

            val javaClassName: String = paramPart.type
            val uiParamType: String = ParamTypeEnum.getByClass(javaClassName)?.name ?: javaClassName

            result += arg.copy(type = uiParamType)
        }

        return result
    }

}