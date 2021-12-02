package com.testerum.scanner.step_lib_scanner.impl

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import com.testerum_api.testerum_steps_api.annotations.hooks.AfterEachTest
import com.testerum_api.testerum_steps_api.annotations.hooks.BeforeAllTests
import com.testerum_api.testerum_steps_api.annotations.hooks.BeforeEachTest
import com.testerum_api.testerum_steps_api.annotations.util.annotationNullToRealNull
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo

val HOOKS_METHOD_ANNOTATIONS = setOf<String>(
        BeforeAllTests::class.java.name,
        BeforeEachTest::class.java.name,
        AfterAllTests::class.java.name,
        AfterEachTest::class.java.name
)


fun ClassInfo.getHookDefinitions(): List<HookDef> {
    val result = mutableListOf<HookDef>()

    for (methodInfo in this.methodInfo) {
        result += methodInfo.toHookDefs()
    }

    return result
}

private fun MethodInfo.toHookDefs(): List<HookDef> {
    val annotationInfoList = this.annotationInfo

    val hookAnnotationInfos = annotationInfoList.filter { HOOKS_METHOD_ANNOTATIONS.contains(it.name) }

    return hookAnnotationInfos.map { annotationInfo ->
        createHookFromMethod(
            phaseAnnotation = HookAnnotation(
                annotation = annotationInfo.loadClassAndInstantiate()
            ),
            methodDeclaringClass = this.classInfo.name,
            methodName = this.name
        )
    }
}

private fun createHookFromMethod(
    phaseAnnotation: HookAnnotation,
    methodDeclaringClass: String,
    methodName: String,
): HookDef {
    try {
        return tryToCreateHookFromMethod(phaseAnnotation, methodDeclaringClass, methodName)
    } catch (e: Exception) {
        throw RuntimeException("failed to create step from method [$methodDeclaringClass.$methodName]", e)
    }

}

private fun tryToCreateHookFromMethod(
    phaseAnnotation: HookAnnotation,
    methodDeclaringClass: String,
    methodName: String,
): HookDef {
    return HookDef(
            phase = phaseAnnotation.phase,
            className = methodDeclaringClass,
            methodName = methodName,
            order = phaseAnnotation.order,
            description = phaseAnnotation.description
    )
}

private class HookAnnotation(annotation: Annotation) {
    private val _phase: HookPhase = when (annotation) {
        is BeforeAllTests -> HookPhase.BEFORE_ALL_TESTS
        is BeforeEachTest -> HookPhase.BEFORE_EACH_TEST
        is AfterEachTest -> HookPhase.AFTER_EACH_TEST
        is AfterAllTests -> HookPhase.AFTER_ALL_TESTS
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }

    private val _description: String? = when (annotation) {
        is BeforeAllTests -> annotation.description.annotationNullToRealNull()
        is BeforeEachTest -> annotation.description.annotationNullToRealNull()
        is AfterEachTest -> annotation.description.annotationNullToRealNull()
        is AfterAllTests -> annotation.description.annotationNullToRealNull()
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }

    private val _order: Int = when (annotation) {
        is BeforeAllTests -> annotation.order
        is BeforeEachTest -> annotation.order
        is AfterEachTest -> annotation.order
        is AfterAllTests -> annotation.order
        else -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
    }

    val phase: HookPhase = _phase
    val description: String? = _description
    val order: Int = _order
}
