package com.testerum.common.json_diff.impl.node_comparer.strategy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.testerum.common.json_diff.impl.context.JsonComparerContext
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonNodeComparer

interface JsonNodeComparerStrategy {

    fun compareArrayNodes(expectedNode: ArrayNode, actualNode: JsonNode, context: JsonComparerContext, comparerDispatcher: JsonNodeComparer): JsonCompareResult

    fun compareObjectNodes(expectedNode: ObjectNode, actualNode: JsonNode, context: JsonComparerContext, comparerDispatcher: JsonNodeComparer): JsonCompareResult

}