package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class IsNotNullIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should throw exception for null node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNotNull()", NullNode.getInstance())
        }
    }

    @Test
    fun `should not throw exception for boolean false node`() {
        functionEvaluator.evaluate("@isNotNull()", booleanNode(false))
    }

    @Test
    fun `should not throw exception for boolean true node`() {
        functionEvaluator.evaluate("@isNotNull()", booleanNode(true))
    }

    @Test
    fun `should not throw exception for empty text node`() {
        functionEvaluator.evaluate("@isNotNull()", textNode(""))
    }

    @Test
    fun `should not throw exception for blank text node`() {
        functionEvaluator.evaluate("@isNotNull()", textNode(" \t          \n  "))
    }

    @Test
    fun `should not throw exception for non-blank text node`() {
        functionEvaluator.evaluate("@isNotNull()", textNode("some text"))
    }

    @Test
    fun `should not throw exception for short numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", shortNode(13))
    }

    @Test
    fun `should not throw exception for int numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", intNode(13))
    }

    @Test
    fun `should not throw exception for long numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", longNode(13))
    }

    @Test
    fun `should not throw exception for big integer numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", bigIntegerNode(BigInteger("13")))
    }

    @Test
    fun `should not throw exception for float numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", floatNode(13.5f))
    }

    @Test
    fun `should not throw exception for double numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", doubleNode(13.5))
    }

    @Test
    fun `should not throw exception for decimal numeric node`() {
        functionEvaluator.evaluate("@isNotNull()", decimalNode(BigDecimal("13.5")))
    }

    @Test
    fun `should not throw exception for empty array node`() {
        functionEvaluator.evaluate("@isNotNull()", arrayNode())
    }

    @Test
    fun `should not throw exception for one-int-element array node`() {
        functionEvaluator.evaluate(
                "@isNotNull()",
                arrayNode(
                        intNode(1)
                )
        )
    }

    @Test
    fun `should not throw exception for one-text-element array node`() {
        functionEvaluator.evaluate(
                "@isNotNull()",
                arrayNode(
                        textNode("one")
                )
        )
    }

    @Test
    fun `should not throw exception for two-elements array node`() {
        functionEvaluator.evaluate(
                "@isNotNull()",
                arrayNode(
                        intNode(1),
                        textNode("two")
                )
        )
    }

    @Test
    fun `should not throw exception for empty object node`() {
        functionEvaluator.evaluate("@isNotNull()", objectNode())
    }

    @Test
    fun `should not throw exception for one-field object node`() {
        functionEvaluator.evaluate(
                "@isNotNull()",
                objectNode(
                        "one" to intNode(1)
                )
        )
    }

    @Test
    fun `should not throw exception for two-fields object node`() {
        functionEvaluator.evaluate(
                "@isNotNull()",
                objectNode(
                        "one" to intNode(1),
                        "two" to objectNode(
                                "nested" to textNode("yes")
                        )
                )
        )
    }

}