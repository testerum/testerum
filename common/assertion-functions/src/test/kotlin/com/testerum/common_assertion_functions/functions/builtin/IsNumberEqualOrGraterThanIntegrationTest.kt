package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsNumberEqualOrGraterThanIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should throw exception for null`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12)", NullNode.getInstance())
        }
    }

    @Test
    fun `should be ok for a bigger number`() {
        functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12)", intNode(13))
    }

    @Test
    fun `should be ok for an equal number`() {
        functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12)", intNode(12))
    }

    @Test
    fun `should throw exception for a smaller number`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12)", intNode(11))
        }
    }

    @Test
    fun `should throw exception for a smaller decimal`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", decimalNode(BigDecimal("12.1")))
        }
    }

    @Test
    fun `should be ok for an equal decimal`() {
        functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", decimalNode(BigDecimal("12.2")))
    }

    @Test
    fun `should be ok for a bigger decimal`() {
        functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", decimalNode(BigDecimal("12.3")))
    }

    @Test
    fun `should be ok to compare a decimal with an int`() {
        functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", intNode(13))
    }

    @Test
    fun `should throw exception for integer text`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", textNode("11"))
        }
    }

    @Test
    fun `should throw exception for decimal text`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", textNode("11.3"))
        }
    }

    @Test
    fun `should throw exception for empty String`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", textNode(""))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberEqualOrGraterThan(12.2)", booleanNode(false))
        }
    }
}
