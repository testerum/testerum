package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IsIntegerOrNullIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should properly match null`() {
        functionEvaluator.evaluate("@isIntegerOrNull()", NullNode.getInstance())
    }

    @Test
    fun `should properly match integer`() {
        functionEvaluator.evaluate("@isIntegerOrNull()", intNode(13))
    }

    @Test
    fun `should throw exception for decimal`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isIntegerOrNull()", decimalNode(BigDecimal("13.7")))
        }
    }

    @Test
    fun `should throw exception for integer text`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isIntegerOrNull()", textNode("13"))
        }
    }

    @Test
    fun `should throw exception for decimal text`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isIntegerOrNull()", textNode("13.3"))
        }
    }

    @Test
    fun `should throw exception for empty String`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isIntegerOrNull()", textNode(""))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(AssertionFailedException::class.java) {
            functionEvaluator.evaluate("@isIntegerOrNull()", booleanNode(false))
        }
    }
}
