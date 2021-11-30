package com.testerum.common_assertion_functions.functions

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AssertionFunction(
        /** function name; if missing, the name of the method will be used instead */
        val functionName: String = USE_METHOD_NAME
)

/**
 * This is an artificial arrangement of 16 unicode characters,
 * with its sole purpose being to never match user-declared values.
 * 
 * @see AssertionFunction.functionName
 */
const val USE_METHOD_NAME = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n"
