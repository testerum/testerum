package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsArrayWithMoreElementsThanIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should throw exception for an empty array`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(0)", arrayNode(ObjectMapper().readTree(""" [] """)))
        }
    }

    @Test
    fun `should throw exception for an array with equal amount of elements - 1`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", arrayNode(ObjectMapper().readTree(""" [{"f1":"Hello"}] """)))
        }
    }

    @Test
    fun `should throw exception for an array with equal amount of elements - 3`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(3)", arrayNode(ObjectMapper().readTree(""" [{"f1":"Hello"}, {"f1":"Hello"}, {"f1":"Hello"}] """)))
        }
    }

    @Test
    fun `should be ok for an array with more elements 2 - 3`() {
        functionEvaluator.evaluate("@isArrayWithMoreElementsThan(2)", arrayNode(ObjectMapper().readTree(""" [{"f1":"Hello"}, {"f1":"Hello"}, {"f1":"Hello"}] """)))
    }

    @Test
    fun `should be ok for an array with more elements 0 - 3`() {
        functionEvaluator.evaluate("@isArrayWithMoreElementsThan(0)", arrayNode(ObjectMapper().readTree(""" [{"f1":"Hello"}, {"f1":"Hello"}, {"f1":"Hello"}] """)))
    }

    @Test
    fun `should throw exception for null`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", NullNode.getInstance())
        }
    }

    @Test
    fun `should throw exception if is called without parameter`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan()", arrayNode(ObjectMapper().readTree(""" [{"f1":"Hello"}] """)))
        }
    }

    @Test
    fun `should throw exception for string`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", textNode("super"))
        }
    }

    @Test
    fun `should throw exception for decimal`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", decimalNode(BigDecimal("13.7")))
        }
    }

    @Test
    fun `should throw exception for integer`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", intNode(13))
        }
    }

    @Test
    fun `should throw exception for object`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", objectNode(Pair("objectName", ObjectMapper().readTree(""" {"f1":"Hello"} """))))
        }
    }

    @Test
    fun `should throw exception for empty String`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", textNode(""))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithMoreElementsThan(1)", booleanNode(false))
        }
    }
}
