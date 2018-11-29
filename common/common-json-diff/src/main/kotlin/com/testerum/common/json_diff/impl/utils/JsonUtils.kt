package com.testerum.common.json_diff.impl.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.testerum.common.json_diff.impl.compare_mode.CompareModeFinder
import com.testerum.common.json_diff.json_path.JsonPath

fun childrenIgnoringCompareModeForObject(objectNode: ObjectNode): Map<String, JsonNode> {
    if (objectNode.size() == 0) {
        return emptyMap()
    }

    val result = mutableMapOf<String, JsonNode>()

    objectNode
            .fields()
            .asSequence()
            .filter { it.key != CompareModeFinder.OBJECT_COMPARE_MODE_FIELD_NAME }
            .forEach { result.put(it.key, it.value) }

    return result
}

fun childrenIgnoringCompareModeForArray(arrayNode: ArrayNode, path: JsonPath): MutableList<JsonNode> {
    if (arrayNode.size() == 0) {
        return mutableListOf()
    }

    val compareMode = CompareModeFinder.getCompareModeForArrayNode(arrayNode, path)
    if (compareMode == null) {
        return arrayNode.toMutableList()
    } else {
        return arrayNode.toMutableList()
                        .subList(1, arrayNode.size())
    }
}
