package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.node.NullNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MatchesNumberIntegrationTest : BaseBuiltinFunctionIntegrationTest() {

    @Test
    fun `should properly match null`() {
        functionEvaluator.evaluate("@matchesNumber(null)", NullNode.getInstance())
    }

    @Test
    fun `should properly match integer`() {
        functionEvaluator.evaluate("@matchesNumber(13)", intNode(13))
    }

    @Test
    fun `should properly match decimal`() {
        functionEvaluator.evaluate("@matchesNumber(13.7)", decimalNode(BigDecimal("13.7")))
    }

    @Test
    fun `should properly match integer text`() {
        functionEvaluator.evaluate("@matchesNumber('13')", intNode(13))
    }

    @Test
    fun `should properly match decimal text`() {
        functionEvaluator.evaluate("@matchesNumber('13.7')", decimalNode(BigDecimal("13.7")))
    }

    @Test
    fun `should throw exception for invalid decimal text`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            functionEvaluator.evaluate("@matchesNumber('12x3')", textNode("12x3"))
        }
    }

    @Test
    fun `should throw exception for boolean`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            functionEvaluator.evaluate("@matchesNumber(false)", booleanNode(false))
        }
    }

}