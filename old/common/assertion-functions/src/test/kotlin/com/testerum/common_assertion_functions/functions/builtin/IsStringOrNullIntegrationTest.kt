package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsStringOrNullIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should be ok for null`() {
        functionEvaluator.evaluate("@isStringOrNull()", NullNode.getInstance())
    }

    @Test
    fun `should properly match string`() {
        functionEvaluator.evaluate("@isStringOrNull()", textNode("super"))
    }

    @Test
    fun `should throw exception for decimal`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isStringOrNull()", decimalNode(BigDecimal("13.7")))
        }
    }

    @Test
    fun `should throw exception for integer`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isStringOrNull()", intNode(13))
        }
    }

    @Test
    fun `should throw exception for object`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isStringOrNull()", objectNode(Pair("objectName", ObjectMapper().readTree(""" {"f1":"Hello"} """))))
        }
    }

    @Test
    fun `should be ok for empty String`() {
        functionEvaluator.evaluate("@isStringOrNull()", textNode(""))
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isStringOrNull()", booleanNode(false))
        }
    }
}
