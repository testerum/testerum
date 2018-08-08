package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import com.testerum.common_assertion_functions.evaluator.FunctionEvaluator
import com.testerum.common_assertion_functions.module_factory.CommonAssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import org.junit.jupiter.api.AfterAll
import java.math.BigDecimal
import java.math.BigInteger

abstract class BaseBuiltinFunctionIntegrationTest {

    companion object {
        private val CONTEXT = ModuleFactoryContext()
        private val FUNCTION_EVALUATOR = CommonAssertionFunctionsModuleFactory(CONTEXT).functionEvaluator

        @AfterAll
        @JvmStatic
        fun tearDown() {
            CONTEXT.shutdown()
        }
    }

    protected val functionEvaluator: FunctionEvaluator = FUNCTION_EVALUATOR

    protected fun booleanNode(boolean: Boolean) = BooleanNode.valueOf(boolean)
    protected fun textNode(text: String) = TextNode.valueOf(text)
    protected fun shortNode(short: Short) = ShortNode.valueOf(short)
    protected fun intNode(int: Int) = IntNode.valueOf(int)
    protected fun longNode(long: Long) = LongNode.valueOf(long)
    protected fun bigIntegerNode(bigInteger: BigInteger) = BigIntegerNode.valueOf(bigInteger)
    protected fun floatNode(float: Float) = FloatNode.valueOf(float)
    protected fun doubleNode(double: Double) = DoubleNode.valueOf(double)
    protected fun decimalNode(bigDecimal: BigDecimal) = DecimalNode.valueOf(bigDecimal)

    protected fun arrayNode(vararg items: JsonNode)
            = ArrayNode(
            JsonNodeFactory.instance,
            items.asList()
    )

    protected fun objectNode(vararg fields: Pair<String, JsonNode>)
            = ObjectNode(
            JsonNodeFactory.instance,
            mapOf(*fields)
    )

}