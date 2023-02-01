package com.testerum.common.json_diff_util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NumericNode
import java.math.BigDecimal
import java.math.BigInteger

fun areNumericNodesEqual(expectedNode: NumericNode, actualNode: JsonNode): Boolean {
    val expected = expectedNode.numberValue()
    val actual = actualNode.numberValue()

    if (expected.canBeConvertedToBigDecimal()) {
        if (actual.canBeConvertedToBigDecimal()) {
            return areValuesEqual(
                    expected.toBigDecimal(),
                    actual.toBigDecimal()
            )
        } else {
            return false
        }
    } else {
        if (actual.canBeConvertedToBigDecimal()) {
            return false
        } else {
            // both expected and actual and positive/negative Infinity or NaN: test if they are actual equal
            return areValuesEqual(
                    expected.getSpecialDecimalValue(),
                    actual.getSpecialDecimalValue()
            )
        }
    }
}

fun <T : Comparable<T>> areValuesEqual(expected: T, actual: T): Boolean {
    // using compareTo() instead of equals() because BigDecimal.equals() also takes the scale into consideration, returning false if the numbers are equal, but have different scales
    return actual.compareTo(expected) == 0
}

fun Number.canBeConvertedToBigDecimal(): Boolean = when (this) {
    is Short, is Int, is Long, is BigInteger -> true
    is Float -> this.toFloat().isFinite()
    is Double -> this.toDouble().isFinite()
    is BigDecimal -> true
    else -> throw IllegalArgumentException("unknown number type [${this.javaClass}]")
}

fun Number.toBigDecimal(): BigDecimal = when (this) {
    is Short -> BigDecimal(this.toInt())
    is Int -> BigDecimal(this)
    is Long -> BigDecimal(this)
    is BigInteger -> BigDecimal(this)
    is Float -> BigDecimal(this.toDouble())
    is Double -> BigDecimal(this)
    is BigDecimal -> this
    else -> throw IllegalArgumentException("unknown number type [${this.javaClass}]")
}

private fun Number.isNaN(): Boolean = when (this) {
    is Short, is Int, is Long, is BigInteger, is BigDecimal -> false
    is Float -> this.toFloat() == Float.NaN
    is Double -> this.toDouble() == Double.NaN
    else -> throw IllegalArgumentException("unknown number type [${this.javaClass}]")
}

private fun Number.isPositiveInfinity(): Boolean = when (this) {
    is Short, is Int, is Long, is BigInteger, is BigDecimal -> false
    is Float -> this.toFloat() == Float.POSITIVE_INFINITY
    is Double -> this.toDouble() == Double.POSITIVE_INFINITY
    else -> throw IllegalArgumentException("unknown number type [${this.javaClass}]")
}

private fun Number.isNegativeInfinity(): Boolean = when (this) {
    is Short, is Int, is Long, is BigInteger, is BigDecimal -> false
    is Float -> this.toFloat() == Float.NEGATIVE_INFINITY
    is Double -> this.toDouble() == Double.NEGATIVE_INFINITY
    else -> throw IllegalArgumentException("unknown number type [${this.javaClass}]")
}

fun Number.getSpecialDecimalValue(): SpecialDecimalValue {
    if (this.isNegativeInfinity()) {
        return SpecialDecimalValue.NEGATIVE_INFINITY
    } else if (this.isPositiveInfinity()) {
        return SpecialDecimalValue.POSITIVE_INFINITY
    } else if (this.isNaN()) {
        return SpecialDecimalValue.NaN
    }

    throw IllegalArgumentException("not a special value [$this]")
}

enum class SpecialDecimalValue {
    NEGATIVE_INFINITY, POSITIVE_INFINITY, NaN
}
