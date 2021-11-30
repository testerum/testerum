package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsNumberOrNullIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should properly match null`() {
        functionEvaluator.evaluate("@isNumberOrNull()", NullNode.getInstance())
    }

    @Test
    fun `should properly match integer`() {
        functionEvaluator.evaluate("@isNumberOrNull()", intNode(13))
    }

    @Test
    fun `should properly match decimal`() {
        functionEvaluator.evaluate("@isNumberOrNull()", decimalNode(BigDecimal("13.7")))
    }

    @Test
    fun `should throw exception for integer text`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberOrNull()", textNode("13"))
        }
    }

    @Test
    fun `should throw exception for decimal text`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberOrNull()", textNode("13.3"))
        }
    }

    @Test
    fun `should throw exception for empty String`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberOrNull()", textNode(""))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isNumberOrNull()", booleanNode(false))
        }
    }
}
