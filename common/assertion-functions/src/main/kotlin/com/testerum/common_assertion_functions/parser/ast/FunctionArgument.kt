package com.testerum.common_assertion_functions.parser.ast

import java.math.BigDecimal

sealed class FunctionArgument

object NullFunctionArgument : FunctionArgument() {
    override fun toString() = "null"
}

data class BooleanFunctionArgument(val value: Boolean) : FunctionArgument() {
    override fun toString() = value.toString()
}

sealed class NumberFunctionArgument : FunctionArgument()

data class IntegerFunctionArgument(val value: Int) : NumberFunctionArgument() {
    override fun toString() = value.toString()
}

data class DecimalFunctionArgument(val value: BigDecimal) : NumberFunctionArgument() {

    constructor(value: String): this(BigDecimal(value))
    
    override fun toString() = value.toPlainString()
}

data class TextFunctionArgument(val value: String) : FunctionArgument() {
    override fun toString() = "'${value.escaped()}'"

    private fun String.escaped(): String = this.replace("\\", "\\\\").replace("'", "\\'")
}
