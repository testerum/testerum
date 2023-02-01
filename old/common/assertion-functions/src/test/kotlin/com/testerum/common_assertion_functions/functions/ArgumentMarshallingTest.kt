package com.testerum.common_assertion_functions.functions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.testerum.common_assertion_functions.evaluator.FunctionEvaluator
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuterFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ArgumentMarshallingTest {

    companion object {
        private val FUNCTION_EVALUATOR = FunctionEvaluator(
            DelegatingFunctionExecuterFactory.createDelegatingFunctionExecuter(
                listOf(MyFunctions)
            )
        )
    }

    @Test
    fun `arguments count - less than expected`() {
        val exception: IllegalArgumentException = assertThrows(IllegalArgumentException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithThreeArguments(1, 2)", IntNode(1))
        }

        assertThat(exception.message)
            .isEqualTo("function [functionWithThreeArguments] expects 3 arguments, but got 2")
    }

    @Test
    fun `arguments count - more than expected`() {
        val exception: IllegalArgumentException = assertThrows(IllegalArgumentException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithOneArgument(1, 2, 3)", IntNode(1))
        }

        assertThat(exception.message)
            .isEqualTo("function [functionWithOneArgument] expects 1 arguments, but got 3")
    }

    @Test
    fun `argument type - boolean primitive`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithBooleanPrimitiveArgument(true)", IntNode(1))
        }

        assert(exception.value is Boolean)
        assertThat(exception.value as Boolean).isTrue
    }

    @Test
    fun `argument type - boolean object null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithBooleanObjectArgument(null)", IntNode(1))
        }

        assertThat(exception.value).isNull()
    }

    @Test
    fun `argument type - boolean object non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithBooleanObjectArgument(false)", IntNode(1))
        }

        assert(exception.value is Boolean)
        assertThat(exception.value as Boolean).isFalse
    }

    @Test
    fun `argument type - integer primitive`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithIntegerPrimitiveArgument(64)", IntNode(1))
        }

        assert(exception.value is Int)
        assertThat(exception.value as Int).isEqualTo(64)
    }

    @Test
    fun `argument type - integer object null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithIntegerObjectArgument(null)", IntNode(1))
        }

        assertThat(exception.value).isNull()
    }

    @Test
    fun `argument type - integer object non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithIntegerObjectArgument(12)", IntNode(1))
        }

        assert(exception.value is Int)
        assertThat(exception.value as Int).isEqualTo(12)
    }

    @Test
    fun `argument type - decimal null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithBigDecimalArgument(null)", IntNode(1))
        }

        assertThat(exception.value).isNull()
    }

    @Test
    fun `argument type - decimal non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithBigDecimalArgument(12.3)", IntNode(1))
        }

        assert(exception.value is BigDecimal)
        assertThat(exception.value as BigDecimal).isEqualTo(BigDecimal("12.3"))
    }

    @Test
    fun `argument type - string null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithStringArgument(null)", IntNode(1))
        }

        assertThat(exception.value).isNull()
    }

    @Test
    fun `argument type - string non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            FUNCTION_EVALUATOR.evaluate("@functionWithStringArgument('some text')", IntNode(1))
        }

        assert(exception.value is String)
        assertThat(exception.value as String).isEqualTo("some text")
    }

}

@Suppress("unused", "UNUSED_PARAMETER")
object MyFunctions {

    @AssertionFunction
    fun functionWithOneArgument(
        actual: JsonNode,
        arg1: Int
    ) {
    }

    @AssertionFunction
    fun functionWithThreeArguments(
        actual: JsonNode,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) {
    }

    @AssertionFunction
    fun functionWithBooleanPrimitiveArgument(
        actual: JsonNode,
        value: Boolean
    ) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithBooleanObjectArgument(
        actual: JsonNode,
        value: Boolean?
    ) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithIntegerPrimitiveArgument(
        actual: JsonNode,
        value: Int
    ) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithIntegerObjectArgument(
        actual: JsonNode,
        value: Int?
    ) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithBigDecimalArgument(
        actual: JsonNode,
        value: BigDecimal?
    ) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithStringArgument(
        actual: JsonNode,
        value: String?
    ) {
        throw ExpectedException(value)
    }

}

class ExpectedException(val value: Any?) : RuntimeException()
