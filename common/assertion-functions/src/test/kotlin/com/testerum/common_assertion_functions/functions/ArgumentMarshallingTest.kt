package com.testerum.common_assertion_functions.functions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.testerum.common_assertion_functions.evaluator.FunctionEvaluator
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuterFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import java.math.BigDecimal

class ArgumentMarshallingTest {

    companion object {
        private val evaluator = FunctionEvaluator(
                DelegatingFunctionExecuterFactory.createDelegatingFunctionExecuter(
                        listOf(MyFunctions)
                )
        )
    }

    @Test
    fun `arguments count - less than expected`() {
        val exception: IllegalArgumentException = assertThrows(IllegalArgumentException::class.java) {
            evaluator.evaluate("@functionWithThreeArguments(1, 2)", IntNode(1))
        }

        assertThat(exception.message, equalTo("function [functionWithThreeArguments] expects 3 arguments, but got 2"))
    }

    @Test
    fun `arguments count - more than expected`() {
        val exception: IllegalArgumentException = assertThrows(IllegalArgumentException::class.java) {
            evaluator.evaluate("@functionWithOneArgument(1, 2, 3)", IntNode(1))
        }

        assertThat(exception.message, equalTo("function [functionWithOneArgument] expects 1 arguments, but got 3"))
    }

    @Test
    fun `argument type - boolean primitive`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithBooleanPrimitiveArgument(true)", IntNode(1))
        }

        assert(exception.value is Boolean)
        assertThat(exception.value as Boolean, equalTo(true))
    }

    @Test
    fun `argument type - boolean object null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithBooleanObjectArgument(null)", IntNode(1))
        }

        assertThat(exception.value, equalTo<Any>(null))
    }

    @Test
    fun `argument type - boolean object non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithBooleanObjectArgument(false)", IntNode(1))
        }

        assert(exception.value is Boolean)
        assertThat(exception.value as Boolean, equalTo(false))
    }

    @Test
    fun `argument type - integer primitive`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithIntegerPrimitiveArgument(64)", IntNode(1))
        }

        assert(exception.value is Int)
        assertThat(exception.value as Int, equalTo(64))
    }

    @Test
    fun `argument type - integer object null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithIntegerObjectArgument(null)", IntNode(1))
        }

        assertThat(exception.value, equalTo<Any>(null))
    }

    @Test
    fun `argument type - integer object non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithIntegerObjectArgument(12)", IntNode(1))
        }

        assert(exception.value is Int)
        assertThat(exception.value as Int, equalTo(12))
    }

    @Test
    fun `argument type - decimal null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithBigDecimalArgument(null)", IntNode(1))
        }

        assertThat(exception.value, equalTo<Any>(null))
    }

    @Test
    fun `argument type - decimal non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithBigDecimalArgument(12.3)", IntNode(1))
        }

        assert(exception.value is BigDecimal)
        assertThat(exception.value as BigDecimal, equalTo(BigDecimal("12.3")))
    }

    @Test
    fun `argument type - string null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithStringArgument(null)", IntNode(1))
        }

        assertThat(exception.value, equalTo<Any>(null))
    }

    @Test
    fun `argument type - string non null`() {
        val exception: ExpectedException = assertThrows(ExpectedException::class.java) {
            evaluator.evaluate("@functionWithStringArgument('some text')", IntNode(1))
        }

        assert(exception.value is String)
        assertThat(exception.value as String, equalTo("some text"))
    }

}

@Suppress("UNUSED_PARAMETER")
object MyFunctions {

    @AssertionFunction
    fun functionWithOneArgument(actual: JsonNode,
                                arg1: Int) {
    }

    @AssertionFunction
    fun functionWithThreeArguments(actual: JsonNode,
                                   arg1: Int,
                                   arg2: Int,
                                   arg3: Int) {
    }

    @AssertionFunction
    fun functionWithBooleanPrimitiveArgument(actual: JsonNode,
                                             value: Boolean) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithBooleanObjectArgument(actual: JsonNode,
                                          value: Boolean?) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithIntegerPrimitiveArgument(actual: JsonNode,
                                             value: Int) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithIntegerObjectArgument(actual: JsonNode,
                                          value: Int?) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithBigDecimalArgument(actual: JsonNode,
                                       value: BigDecimal?) {
        throw ExpectedException(value)
    }

    @AssertionFunction
    fun functionWithStringArgument(actual: JsonNode,
                                   value: String?) {
        throw ExpectedException(value)
    }

}

class ExpectedException(val value: Any?) : RuntimeException()
