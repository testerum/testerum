package com.testerum.common.json_diff.impl.node_comparer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import com.testerum.common.json_diff.impl.compare_mode.JsonCompareMode
import com.testerum.common.json_diff.impl.context.JsonComparerContext
import com.testerum.common.json_diff.impl.node_comparer.strategy.JsonNodeComparerStrategy
import com.testerum.common.json_diff_util.areNumericNodesEqual
import com.testerum.common.json_diff_util.areValuesEqual
import com.testerum.common_assertion_functions.evaluator.FunctionEvaluator

class JsonNodeComparer(private val functionEvaluator: FunctionEvaluator,
                       private val comparerStrategies: Map<JsonCompareMode, JsonNodeComparerStrategy>) {

    fun compareNodes(expectedNode: JsonNode, actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        return when (expectedNode) {
            is NullNode -> compareNullNodes(actualNode, context)
            is BooleanNode -> compareBooleanNodes(expectedNode, actualNode, context)
            is NumericNode -> compareNumericNodes(expectedNode, actualNode, context)
            is TextNode -> compareTextNodes(expectedNode, actualNode, context)
            is ArrayNode -> compareArrayNodes(expectedNode, actualNode, context)
            is ObjectNode -> compareObjectNodes(expectedNode, actualNode, context)
            else -> throw IllegalArgumentException("unsupported node type [${expectedNode.javaClass.name}] at ${context.path}")
        }
    }

    private fun compareNullNodes(actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        if (actualNode !is NullNode) {
            return DifferentJsonCompareResult("expected null, but got a node of type [${actualNode.javaClass.simpleName}] instead", context.path)
        }

        return EqualJsonCompareResult
    }

    private fun compareBooleanNodes(expectedNode: BooleanNode, actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        if (actualNode !is BooleanNode) {
            return DifferentJsonCompareResult("mismatched node types: expected [${BooleanNode::class.java.simpleName}], but got [${actualNode.javaClass.simpleName}] instead", context.path)
        }

        return compareValues(
                expectedNode.booleanValue(),
                actualNode.booleanValue(),
                context
        )
    }

    private fun compareNumericNodes(expectedNode: NumericNode, actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        if (actualNode !is NumericNode) {
            return DifferentJsonCompareResult("mismatched node types: expected [${NumericNode::class.java.simpleName}], but got [${actualNode.javaClass.simpleName}] instead", context.path)
        }

        if (!areNumericNodesEqual(expectedNode, actualNode)) {
            return DifferentJsonCompareResult("mismatched values: expected [${expectedNode.numberValue()}], but got [${actualNode.numberValue()}] instead", context.path)
        }

        return EqualJsonCompareResult
    }

    private fun compareTextNodes(expectedNode: TextNode, actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        val expectedTextValue = expectedNode.textValue()
        if (expectedTextValue.startsWith("@")) {
            // todo: also change functions not to throw exceptions
            functionEvaluator.evaluate(expectedTextValue, actualNode)

            return EqualJsonCompareResult
        }

        if (actualNode !is TextNode) {
            return DifferentJsonCompareResult("mismatched node types: expected [${TextNode::class.java.simpleName}], but got [${actualNode.javaClass.simpleName}] instead", context.path)
        }

        return compareValues(
                expectedTextValue,
                actualNode.textValue(),
                context
        )
    }

    private fun compareArrayNodes(expectedNode: ArrayNode, actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        val compareMode: JsonCompareMode = context.getCompareMode()
        val nodeComparerStrategy: JsonNodeComparerStrategy = getStrategy(compareMode)

        return nodeComparerStrategy.compareArrayNodes(expectedNode, actualNode, context, this)
    }

    private fun compareObjectNodes(expectedNode: ObjectNode, actualNode: JsonNode, context: JsonComparerContext): JsonCompareResult {
        val compareMode: JsonCompareMode = context.getCompareMode()
        val nodeComparerStrategy: JsonNodeComparerStrategy = getStrategy(compareMode)

        return nodeComparerStrategy.compareObjectNodes(expectedNode, actualNode, context, this)
    }

    private fun <T : Comparable<T>> compareValues(expected: T, actual: T, context: JsonComparerContext): JsonCompareResult {
        if (!areValuesEqual(expected, actual)) {
            return DifferentJsonCompareResult("mismatched values: expected [$expected], but got [$actual] instead", context.path)
        }

        return EqualJsonCompareResult
    }

    private fun getStrategy(compareMode: JsonCompareMode): JsonNodeComparerStrategy
            = comparerStrategies[compareMode] ?: throw IllegalArgumentException("cannot find comparer strategy for mode [$compareMode]")

}