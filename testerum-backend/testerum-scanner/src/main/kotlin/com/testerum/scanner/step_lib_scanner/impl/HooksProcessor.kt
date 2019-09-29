package com.testerum.scanner.step_lib_scanner.impl

import com.testerum.api.annotations.hooks.AfterAllTests
import com.testerum.api.annotations.hooks.AfterEachTest
import com.testerum.api.annotations.hooks.BeforeAllTests
import com.testerum.api.annotations.hooks.BeforeEachTest
import com.testerum.api.annotations.util.annotationNullToRealNull
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import java.lang.reflect.Method

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
                method = this.loadClassAndGetMethod()
        )
    }
}

private fun createHookFromMethod(phaseAnnotation: HookAnnotation, method: Method): HookDef {
    try {
        return tryToCreateHookFromMethod(phaseAnnotation, method)
    } catch (e: Exception) {
        throw RuntimeException("failed to create step from method [$method]", e)
    }

}

private fun tryToCreateHookFromMethod(phaseAnnotation: HookAnnotation, method: Method): HookDef {
    return HookDef(
            phase = phaseAnnotation.phase,
            className = method.declaringClass.name,
            methodName = method.name,
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
