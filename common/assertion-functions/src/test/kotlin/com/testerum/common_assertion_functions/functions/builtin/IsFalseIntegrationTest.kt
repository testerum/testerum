package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class IsFalseIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should throw exception for null node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", NullNode.getInstance())
        }
    }

    @Test
    fun `should not throw exception for boolean false node`() {
        functionEvaluator.evaluate("@isFalse()", booleanNode(false))
    }

    @Test
    fun `should throw exception for boolean true node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", booleanNode(true))
        }
    }

    @Test
    fun `should throw exception for empty text node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", textNode(""))
        }
    }

    @Test
    fun `should not throw exception for empty text node when matching`() {
        functionEvaluator.evaluate("@isFalse()", textNode("false"))
    }

    @Test
    fun `should throw exception for short numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", shortNode(13))
        }
    }

    @Test
    fun `should throw exception for int numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", intNode(13))
        }
    }

    @Test
    fun `should throw exception for long numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", longNode(13))
        }
    }

    @Test
    fun `should throw exception for big integer numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", bigIntegerNode(BigInteger("13")))
        }
    }

    @Test
    fun `should throw exception for float numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", floatNode(13.5f))
        }
    }

    @Test
    fun `should throw exception for double numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", doubleNode(13.5))
        }
    }

    @Test
    fun `should throw exception for decimal numeric node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", decimalNode(BigDecimal("13.5")))
        }
    }

    @Test
    fun `should throw exception for empty array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", arrayNode())
        }
    }

    @Test
    fun `should throw exception for one-int-element array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isFalse()",
                    arrayNode(
                            intNode(1)
                    )
            )
        }
    }

    @Test
    fun `should throw exception for empty object node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isFalse()", objectNode())
        }
    }

    @Test
    fun `should throw exception for one-field object node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@isFalse()",
                    objectNode(
                            "one" to intNode(1)
                    )
            )
        }
    }

}