package com.testerum.common_assertion_functions.executer.arg_marshaller

import com.testerum.common_assertion_functions.parser.ast.*
import java.math.BigDecimal

object FunctionArgMarshaller {

    @JvmStatic
    fun argToBooleanPrimitive(arg: FunctionArgument,
                              argIndex: Int): Boolean {
        if (arg !is BooleanFunctionArgument) {
            throw IllegalArgumentException(
                    "argument number $argIndex is of incorrect type" +
                            ": expected boolean, but got a ${arg.javaClass.simpleName} instead"
            )
        }

        return arg.value
    }

    @JvmStatic
    fun argToBooleanObject(arg: FunctionArgument,
                           argIndex: Int): Boolean? {
        if (arg is NullFunctionArgument) {
            return null
        }

        if (arg !is BooleanFunctionArgument) {
            throw IllegalArgumentException(
                    "argument number $argIndex is of incorrect type" +
                    ": expected Boolean, but got a ${arg.javaClass.simpleName} instead"
            )
        }

        return arg.value
    }

    @JvmStatic
    fun argToIntegerPrimitive(arg: FunctionArgument,
                              argIndex: Int): Int {
        if (arg !is IntegerFunctionArgument) {
            throw IllegalArgumentException(
                    "argument number $argIndex is of incorrect type" +
                    ": expected int, but got a ${arg.javaClass.simpleName} instead"
            )
        }

        return arg.value
    }

    @JvmStatic
    fun argToIntegerObject(arg: FunctionArgument,
                           argIndex: Int): Int? {
        if (arg is NullFunctionArgument) {
            return null
        }

        if (arg !is IntegerFunctionArgument) {
            throw IllegalArgumentException(
                    "argument number $argIndex is of incorrect type" +
                    ": expected Integer, but got a ${arg.javaClass.simpleName} instead"
            )
        }

        return arg.value
    }

    @JvmStatic
    fun argToBigDecimal(arg: FunctionArgument,
                        argIndex: Int): BigDecimal? {
        return when (arg) {
            is NullFunctionArgument -> null
            is DecimalFunctionArgument -> arg.value
            is IntegerFunctionArgument -> BigDecimal(arg.value)
            is TextFunctionArgument -> try {
                BigDecimal(arg.value)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("[${arg.value}] is not a valid BigDecimal")
            }

            else -> throw IllegalArgumentException(
                    "argument number $argIndex is of unexpected type" +
                    ": expected null, BigDecimal, Integer, or String, but got a ${arg.javaClass.simpleName} instead"
            )
        }
    }

    @JvmStatic
    fun argToString(arg: FunctionArgument,
                    argIndex: Int): String? {
        if (arg is NullFunctionArgument) {
            return null
        }

        if (arg !is TextFunctionArgument) {
            throw IllegalArgumentException(
                    "argument number $argIndex is of incorrect type" +
                    ": expected String, but got a ${arg.javaClass.simpleName} instead"
            )
        }

        return arg.value
    }

}