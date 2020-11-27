package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.node.ShortNode
import com.testerum.common.json_diff_util.toBigDecimal
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction
import java.math.BigDecimal

object NumberFunctions {

    @AssertionFunction
    fun isNumber(actualNode: JsonNode) {
        if (actualNode is NumericNode) {
            return
        }

        throw AssertionFailedException("expected a Number, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isNumberOrNull(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }

        if (actualNode is NumericNode) {
            return
        }

        throw AssertionFailedException("expected a Number, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isInteger(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            throw AssertionFailedException("expected a Number, but got ${actualNode.toPrettyString()}")
        }

        if (actualNode is IntNode || actualNode is LongNode || actualNode is ShortNode) {
            return
        }

        throw AssertionFailedException("expected a Number, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isIntegerOrNull(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }

        if (actualNode is IntNode || actualNode is LongNode || actualNode is ShortNode) {
            return
        }

        throw AssertionFailedException("expected an Integer Number, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isNumberLessThan(actualNode: JsonNode, expectedValue: BigDecimal?) {
        isNumber(actualNode)

        val actualValue = actualNode.numberValue()

        if (actualValue.toBigDecimal() < expectedValue) {
            return
        }
        throw AssertionFailedException("expected the Number to be less then ${expectedValue}, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isNumberEqualOrLessThan(actualNode: JsonNode, expectedValue: BigDecimal?) {
        isNumber(actualNode)

        val actualValue = actualNode.numberValue()

        if (actualValue.toBigDecimal() <= expectedValue) {
            return
        }
        throw AssertionFailedException("expected the Number to be less or equal then ${expectedValue}, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isNumberGraterThan(actualNode: JsonNode, expectedValue: BigDecimal?) {
        isNumber(actualNode)

        val actualValue = actualNode.numberValue()

        if (actualValue.toBigDecimal() > expectedValue) {
            return
        }
        throw AssertionFailedException("expected the Number to be bigger then ${expectedValue}, but got ${actualNode.toPrettyString()}")
    }

    @AssertionFunction
    fun isNumberEqualOrGraterThan(actualNode: JsonNode, expectedValue: BigDecimal?) {
        isNumber(actualNode)

        val actualValue = actualNode.numberValue()

        if (actualValue.toBigDecimal() >= expectedValue) {
            return
        }
        throw AssertionFailedException("expected the Number to be bigger or equal then ${expectedValue}, but got ${actualNode.toPrettyString()}")
    }
}
