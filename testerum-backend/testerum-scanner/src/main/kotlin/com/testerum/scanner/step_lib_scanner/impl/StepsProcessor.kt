package com.testerum.scanner.step_lib_scanner.impl

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.annotations.steps.When
import com.testerum.api.annotations.util.annotationNullToRealNull
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.step.BasicStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.ScannerStepPatternParserFactory
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.ParamSimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.SimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.TextSimpleBasicStepPatternPart
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.Collections


val STEPS_METHOD_ANNOTATIONS = setOf<String>(
        Given::class.java.name,
        When::class.java.name,
        Then::class.java.name
)

private val STEP_PATTERN_PARSER = ParserExecuter(ScannerStepPatternParserFactory.pattern())

fun ClassInfo.getStepDefinitions(): List<BasicStepDef> {
    val result = mutableListOf<BasicStepDef>()

    for (methodInfo in this.methodInfo) {
        result += methodInfo.toBasicStepDefs()
    }

    return result
}

private fun MethodInfo.toBasicStepDefs(): List<BasicStepDef> {
    val annotationInfoList = this.annotationInfo

    val stepAnnotationInfos = annotationInfoList.filter { STEPS_METHOD_ANNOTATIONS.contains(it.name) }

    return stepAnnotationInfos.map { annotationInfo ->
        createStepFromMethod(
                stepAnnotation = StepAnnotation(
                        annotation = annotationInfo.loadClassAndInstantiate()
                ),
                method = this.loadClassAndGetMethod()
        )
    }
}

private fun createStepFromMethod(stepAnnotation: StepAnnotation,
                                 method: Method): BasicStepDef {
    try {
        return tryToCreateStepFromMethod(stepAnnotation, method)
    } catch (e: Exception) {
        throw RuntimeException("failed to create step from method [$method]", e)
    }
}

private fun tryToCreateStepFromMethod(stepAnnotation: StepAnnotation, method: Method): BasicStepDef {
    if (stepAnnotation.pattern == "") {
        throw RuntimeException("empty pattern for method [$method]")
    }

    val simplePatternParts: List<SimpleBasicStepPatternPart> = STEP_PATTERN_PARSER.parse(stepAnnotation.pattern)

    val scannerParts = mutableListOf<StepPatternPart>()

    val methodParameters: Array<out Parameter> = method.parameters
    val parametersCountFromPattern = simplePatternParts.count { it is ParamSimpleBasicStepPatternPart }
    if (parametersCountFromPattern != methodParameters.size) {
        throw RuntimeException("parameter count mismatch: pattern [${stepAnnotation.pattern}] has $parametersCountFromPattern parameters, but the method has ${methodParameters.size} parameters")
    }

    var paramIndex = -1

    for (simplePart in simplePatternParts) {
        val scannerPart: StepPatternPart = when (simplePart) {
            is TextSimpleBasicStepPatternPart -> TextStepPatternPart(text = simplePart.text)

            is ParamSimpleBasicStepPatternPart -> {
                val paramName = simplePart.name

                paramIndex++
                val param = methodParameters[paramIndex]

                // todo: move this to a separate method
                val enumValues: List<String>

                val enumConstants: Array<out Any>? = param.type.enumConstants
                enumValues = enumConstants?.map { (it as Enum<*>).name }
                        ?.toList()
                        ?: Collections.emptyList()

                val paramAnnotation: Param? = param.getAnnotation(Param::class.java)

                ParamStepPatternPart(
                        name = paramName,
                        type = param.type.name,
                        description = getParamDescription(paramAnnotation),
                        enumValues = enumValues
                )
            }
        }

        scannerParts += scannerPart
    }

    return BasicStepDef(
            phase = stepAnnotation.phase,
            stepPattern = StepPattern(scannerParts),
            className = method.declaringClass.name,
            methodName = method.name,
            description = stepAnnotation.description,
            tags = stepAnnotation.tags
    )
}

private fun getParamDescription(paramAnnotation: Param?): String? {
    if (paramAnnotation == null) {
        return null
    }

    return paramAnnotation.description.annotationNullToRealNull()
}


private class StepAnnotation(annotation: Annotation) {
    private val _phase: StepPhaseEnum = when (annotation) {
        is Given -> StepPhaseEnum.GIVEN
        is When -> StepPhaseEnum.WHEN
        is Then -> StepPhaseEnum.THEN
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }
    private val _pattern: String = when (annotation) {
        is Given -> annotation.value
        is When -> annotation.value
        is Then -> annotation.value
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }
    private val _description: String? = when (annotation) {
        is Given -> annotation.description.annotationNullToRealNull()
        is When -> annotation.description.annotationNullToRealNull()
        is Then -> annotation.description.annotationNullToRealNull()
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }
    private val _tags: List<String> = when (annotation) {
        is Given -> annotation.tags.toList()
        is When -> annotation.tags.toList()
        is Then -> annotation.tags.toList()
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }


    val phase: StepPhaseEnum = _phase
    val pattern: String = _pattern
    val description: String? = _description
    val tags: List<String> = _tags
}
