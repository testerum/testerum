package com.testerum.common_assertion_functions.parser.ast

data class FunctionCall(val functionName: String,
                        val functionArgs: List<FunctionArgument> = emptyList()) {
    override fun toString() = buildString {
        append('@')
        append(functionName)

        append("(")
        append(functionArgs.joinToString(separator = ", "))
        append(")")
    }
}
