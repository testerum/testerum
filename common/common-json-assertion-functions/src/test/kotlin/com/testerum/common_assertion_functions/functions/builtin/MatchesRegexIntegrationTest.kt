package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class MatchesRegexIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should throw exception for null node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", NullNode.getInstance())
        }
    }

    @Test
    fun `should throw exception for boolean false node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", booleanNode(false))
        }
    }

    @Test
    fun `should not throw exception for boolean false node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('false')", booleanNode(false))
    }

    @Test
    fun `should throw exception for boolean true node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", booleanNode(true))
        }
    }

    @Test
    fun `should not throw exception for boolean true node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('true')", booleanNode(true))
    }

    @Test
    fun `should throw exception for empty text node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", textNode(""))
        }
    }

    @Test
    fun `should not throw exception for empty text node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('.?')", textNode(""))
    }

    @Test
    fun `should throw exception for blank text node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", textNode(" \t          \n  "))
        }
    }

    @Test
    fun `should not throw exception for blank text node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('\\\\s*')", textNode(" \t          \n  "))
    }

    @Test
    fun `should throw exception for non-blank text node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", textNode("some text"))
        }
    }

    @Test
    fun `should not exception for non-blank text node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('some text')", textNode("some text"))
    }

    @Test
    fun `should throw exception for short numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", shortNode(13))
        }
    }

    @Test
    fun `should not throw exception for short numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('\\\\d+')", shortNode(13))
    }

    @Test
    fun `should throw exception for int numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", intNode(13))
        }
    }

    @Test
    fun `should not throw exception for int numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('\\\\d+')", intNode(13))
    }

    @Test
    fun `should throw exception for long numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", longNode(13))
        }
    }

    @Test
    fun `should not throw exception for long numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('\\\\d+')", longNode(13))
    }

    @Test
    fun `should throw exception for big integer numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", bigIntegerNode(BigInteger("13")))
        }
    }

    @Test
    fun `should not throw exception for big integer numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('\\\\d+')", bigIntegerNode(BigInteger("13")))
    }

    @Test
    fun `should throw exception for float numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('[a-z]+')", floatNode(13.5f))
        }
    }

    @Test
    fun `should not throw exception for float numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('[0-9\\\\.]+')", floatNode(13.5f))
    }

    @Test
    fun `should throw exception for double numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('[a-z]+')", doubleNode(13.5))
        }
    }

    @Test
    fun `should not throw exception for double numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('[0-9\\\\.]+')", doubleNode(13.5))
    }

    @Test
    fun `should throw exception for decimal numeric node when not matching`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('[a-z]+')", decimalNode(BigDecimal("13.5")))
        }
    }

    @Test
    fun `should not throw exception for decimal numeric node when matching`() {
        functionEvaluator.evaluate("@matchesRegex('[0-9\\\\.]+')", decimalNode(BigDecimal("13.5")))
    }

    @Test
    fun `should throw exception for empty array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@matchesRegex('regex')", arrayNode())
        }
    }

    @Test
    fun `should throw exception for one-int-element array node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@matchesRegex('regex')",
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
                    "@matchesRegex('regex')",
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
                    "@matchesRegex('regex')",
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
            functionEvaluator.evaluate("@matchesRegex('regex')", objectNode())
        }
    }

    @Test
    fun `should throw exception for one-field object node`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate(
                    "@matchesRegex('regex')",
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
                    "@matchesRegex('regex')",
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