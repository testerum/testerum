package com.testerum.common.json_diff.impl.node_comparer.strategy.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.testerum.common.json_diff.impl.context.JsonComparerContext
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.EqualJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonNodeComparer
import com.testerum.common.json_diff.impl.node_comparer.strategy.JsonNodeComparerStrategy
import com.testerum.common.json_diff.impl.utils.childrenIgnoringCompareModeForArray
import com.testerum.common.json_diff.impl.utils.childrenIgnoringCompareModeForObject

object ExactJsonNodeComparerStrategy : JsonNodeComparerStrategy {

    override fun compareArrayNodes(expectedNode: ArrayNode, actualNode: JsonNode, context: JsonComparerContext, comparerDispatcher: JsonNodeComparer): JsonCompareResult {
        if (actualNode !is ArrayNode) {
            return DifferentJsonCompareResult("mismatched node types: expected ArrayNode, but got ${actualNode.javaClass.simpleName} instead", context.path)
        }

        val expectedChildrenIgnoringCompareMode: List<JsonNode> = childrenIgnoringCompareModeForArray(expectedNode, context.path)
        val actualChildrenIgnoringCompareMode: List<JsonNode> = childrenIgnoringCompareModeForArray(actualNode, context.path)
        if (actualChildrenIgnoringCompareMode.size != expectedChildrenIgnoringCompareMode.size) {
            return DifferentJsonCompareResult("mismatched number of items in the arrays: expected ${expectedChildrenIgnoringCompareMode.size}, but got ${actualChildrenIgnoringCompareMode.size} instead", context.path)
        }

        for ((index, expectedChildItem) in expectedChildrenIgnoringCompareMode.withIndex()) {
            val actualChildItem = actualNode[index]

            val compareResult: JsonCompareResult = comparerDispatcher.compareNodes(
                    expectedChildItem,
                    actualChildItem,
                    context.pushArrayItem(expectedNode, index)
            )
            if (compareResult is DifferentJsonCompareResult) {
                return compareResult
            }
        }

        return EqualJsonCompareResult
    }

    override fun compareObjectNodes(expectedNode: ObjectNode, actualNode: JsonNode, context: JsonComparerContext, comparerDispatcher: JsonNodeComparer): JsonCompareResult {
        if (actualNode !is ObjectNode) {
            return DifferentJsonCompareResult("mismatched node types: expected ObjectNode, but got ${actualNode.javaClass.simpleName} instead", context.path)
        }

        val actualChildrenIgnoringCompareMode: Map<String, JsonNode> = childrenIgnoringCompareModeForObject(actualNode)
        val expectedChildrenIgnoringCompareMode: Map<String, JsonNode> = childrenIgnoringCompareModeForObject(expectedNode)

        // check fields in expected (they should be in actual and have the same value)
        for ((expectedChildFieldName, expectedChildNode) in expectedChildrenIgnoringCompareMode) {
            val actualChildNode: JsonNode = actualChildrenIgnoringCompareMode[expectedChildFieldName]
                    ?: return DifferentJsonCompareResult("expected field [$expectedChildFieldName] not found", context.path)

            val compareResult: JsonCompareResult = comparerDispatcher.compareNodes(
                    expectedChildNode,
                    actualChildNode,
                    context.pushNonArrayItem(expectedNode, expectedChildFieldName, expectedChildNode)
            )
            if (compareResult is DifferentJsonCompareResult) {
                return compareResult
            }
        }

        // check fields in actual (they should be in expected and have the same value)
        for ((actualChildFieldName, actualChildNode) in actualChildrenIgnoringCompareMode) {
            val expectedChildNode: JsonNode = expectedChildrenIgnoringCompareMode[actualChildFieldName]
                    ?: return DifferentJsonCompareResult("unexpected field [$actualChildFieldName] found", context.path)

            val compareResult: JsonCompareResult = comparerDispatcher.compareNodes(
                    expectedChildNode,
                    actualChildNode,
                    context.pushNonArrayItem(expectedNode, actualChildFieldName, expectedChildNode)
            )
            if (compareResult is DifferentJsonCompareResult) {
                return compareResult
            }
        }

        return EqualJsonCompareResult
    }
}