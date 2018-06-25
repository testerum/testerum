package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class IsNotBlankIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should not throw exception for null node`() {
        functionEvaluator.evaluate("@isNotBlank()", NullNode.getInstance())
    }

    @Test
    fun `should not throw exception for boolean false node`() {
        functionEvaluator.evaluate("@isNotBlank()", booleanNode(false))
    }

    @Test
    fun `should not throw exception for boolean true node`() {
        functionEvaluator.evaluate("@isNotBlank()", booleanNode(true))
    }

    @Test
    fun `should throw exception for empty text node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNotBlank()", textNode(""))
        }
    }

    @Test
    fun `should throw exception for blank text node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNotBlank()", textNode(" \t          \n  "))
        }
    }

    @Test
    fun `should not throw exception for non-blank text node`() {
        functionEvaluator.evaluate("@isNotBlank()", textNode("some text"))
    }

    @Test
    fun `should not throw exception for short numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", shortNode(13))
    }

    @Test
    fun `should not throw exception for int numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", intNode(13))
    }

    @Test
    fun `should not throw exception for long numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", longNode(13))
    }

    @Test
    fun `should not throw exception for big integer numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", bigIntegerNode(BigInteger("13")))
    }

    @Test
    fun `should not throw exception for float numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", floatNode(13.5f))
    }

    @Test
    fun `should not throw exception for double numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", doubleNode(13.5))
    }

    @Test
    fun `should not throw exception for decimal numeric node`() {
        functionEvaluator.evaluate("@isNotBlank()", decimalNode(BigDecimal("13.5")))
    }

    @Test
    fun `should not throw exception for empty array node`() {
        functionEvaluator.evaluate("@isNotBlank()", arrayNode())
    }

    @Test
    fun `should not throw exception for one-int-element array node`() {
        functionEvaluator.evaluate(
                "@isNotBlank()",
                arrayNode(
                        intNode(1)
                )
        )
    }

    @Test
    fun `should not throw exception for one-text-element array node`() {
        functionEvaluator.evaluate(
                "@isNotBlank()",
                arrayNode(
                        textNode("one")
                )
        )
    }

    @Test
    fun `should not throw exception for two-elements array node`() {
        functionEvaluator.evaluate(
                "@isNotBlank()",
                arrayNode(
                        intNode(1),
                        textNode("two")
                )
        )
    }

    @Test
    fun `should not throw exception for empty object node`() {
        functionEvaluator.evaluate("@isNotBlank()", objectNode())
    }

    @Test
    fun `should not throw exception for one-field object node`() {
        functionEvaluator.evaluate(
                "@isNotBlank()",
                objectNode(
                        "one" to intNode(1)
                )
        )
    }

    @Test
    fun `should not throw exception for two-fields object node`() {
        functionEvaluator.evaluate(
                "@isNotBlank()",
                objectNode(
                        "one" to intNode(1),
                        "two" to objectNode(
                                "nested" to textNode("yes")
                        )
                )
        )
    }

}