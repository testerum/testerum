package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsArrayWithSizeIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should be ok for an empty array`() {
        functionEvaluator.evaluate("@isArrayWithSize(0)", ObjectMapper().readTree(""" [] """))
    }

    @Test
    fun `should be ok for an array with an element`() {
        functionEvaluator.evaluate("@isArrayWithSize(1)", ObjectMapper().readTree(""" [{"f1":"Hello"}] """))
    }

    @Test
    fun `should be ok for an array with 3 elements`() {
        functionEvaluator.evaluate("@isArrayWithSize(3)", ObjectMapper().readTree(""" [{"f1":"Hello"}, {"f1":"Hello"}, {"f1":"Hello"}] """))
    }

    @Test
    fun `should throw exception for different numbers of elements`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(2)", ObjectMapper().readTree(""" [{"f1":"Hello"}, {"f1":"Hello"}, {"f1":"Hello"}] """))
        }
    }

    @Test
    fun `should throw exception for null`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", NullNode.getInstance())
        }
    }

    @Test
    fun `should throw exception if is called without parameter`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize()", ObjectMapper().readTree(""" [{"f1":"Hello"}] """))
        }
    }

    @Test
    fun `should throw exception for string`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", textNode("super"))
        }
    }

    @Test
    fun `should throw exception for decimal`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", decimalNode(BigDecimal("13.7")))
        }
    }

    @Test
    fun `should throw exception for integer`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", intNode(13))
        }
    }

    @Test
    fun `should throw exception for object`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", objectNode(Pair("objectName", ObjectMapper().readTree(""" {"f1":"Hello"} """))))
        }
    }

    @Test
    fun `should throw exception for empty String`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", textNode(""))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isArrayWithSize(1)", booleanNode(false))
        }
    }
}
