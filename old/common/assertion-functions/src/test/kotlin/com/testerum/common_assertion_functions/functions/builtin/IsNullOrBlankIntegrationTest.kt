package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class IsNullOrBlankIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should not throw exception for null node`() {
        functionEvaluator.evaluate("@isNullOrBlank()", NullNode.getInstance())
    }

    @Test
    fun `should throw exception for boolean false node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", booleanNode(false))
        }
    }

    @Test
    fun `should throw exception for boolean true node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", booleanNode(true))
        }
    }

    @Test
    fun `should not throw exception for empty text node`() {
        functionEvaluator.evaluate("@isNullOrBlank()", textNode(""))
    }

    @Test
    fun `should not throw exception for blank text node`() {
        functionEvaluator.evaluate("@isNullOrBlank()", textNode(" \t          \n  "))
    }

    @Test
    fun `should throw exception for non-blank text node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", textNode("some text"))
        }
    }

    @Test
    fun `should throw exception for short numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", shortNode(13))
        }
    }

    @Test
    fun `should throw exception for int numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", intNode(13))
        }
    }

    @Test
    fun `should throw exception for long numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", longNode(13))
        }
    }

    @Test
    fun `should throw exception for big integer numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", bigIntegerNode(BigInteger("13")))
        }
    }

    @Test
    fun `should throw exception for float numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", floatNode(13.5f))
        }
    }

    @Test
    fun `should throw exception for double numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", doubleNode(13.5))
        }
    }

    @Test
    fun `should throw exception for decimal numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", decimalNode(BigDecimal("13.5")))
        }
    }

    @Test
    fun `should throw exception for empty array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", arrayNode())
        }
    }

    @Test
    fun `should throw exception for one-int-element array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isNullOrBlank()",
                    arrayNode(
                            intNode(1)
                    )
            )
        }
    }

    @Test
    fun `should throw exception for one-text-element array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isNullOrBlank()",
                    arrayNode(
                            textNode("one")
                    )
            )
        }
    }

    @Test
    fun `should throw exception for two-elements array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isNullOrBlank()",
                    arrayNode(
                            intNode(1),
                            textNode("two")
                    )
            )
        }
    }

    @Test
    fun `should throw exception for empty object node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNullOrBlank()", objectNode())
        }
    }

    @Test
    fun `should throw exception for one-field object node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isNullOrBlank()",
                    objectNode(
                            "one" to intNode(1)
                    )
            )
        }
    }

    @Test
    fun `should throw exception for two-fields object node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isNullOrBlank()",
                    objectNode(
                            "one" to intNode(1),
                            "two" to objectNode(
                                    "nested" to textNode("yes")
                            )
                    )
            )
        }
    }

}