package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsObjectOrNullIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should be ok for null`() {
        functionEvaluator.evaluate("@isObjectOrNull()", NullNode.getInstance())
    }

    @Test
    fun `should throw exception for string`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isObjectOrNull()", textNode("super"))
        }
    }

    @Test
    fun `should throw exception for decimal`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isObjectOrNull()", decimalNode(BigDecimal("13.7")))
        }
    }

    @Test
    fun `should throw exception for integer`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isObjectOrNull()", intNode(13))
        }
    }

    @Test
    fun `should be ok for object`() {
        functionEvaluator.evaluate("@isObjectOrNull()", objectNode(Pair("objectName", ObjectMapper().readTree(""" {"f1":"Hello"} """))))
    }

    @Test
    fun `should throw exception for array`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isObjectOrNull()", arrayNode(ObjectMapper().readTree(""" [{"f1":"Hello"}] """)))
        }
    }

    @Test
    fun `should throw exception for empty String`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isObjectOrNull()", textNode(""))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isObjectOrNull()", booleanNode(false))
        }
    }
}
