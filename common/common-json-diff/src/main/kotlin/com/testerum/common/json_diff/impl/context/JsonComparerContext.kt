package com.testerum.common.json_diff.impl.context

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.testerum.common.json_diff.impl.compare_mode.CompareModeFinder
import com.testerum.common.json_diff.impl.compare_mode.JsonCompareMode
import com.testerum.common.json_diff.json_path.JsonArrayItemPathElement
import com.testerum.common.json_diff.json_path.JsonNodePathElement
import com.testerum.common.json_diff.json_path.JsonPath

class JsonComparerContext private constructor(val path: JsonPath = JsonPath.EMPTY) {

    companion object {
        val EMPTY = JsonComparerContext()
    }

    fun pushArrayItem(parent: ArrayNode, index: Int): JsonComparerContext {
        return JsonComparerContext(
                path.push(
                        JsonArrayItemPathElement(parent, index)
                )
        )
    }

    fun pushNonArrayItem(parent: ObjectNode?, fieldName: String, fieldValue: JsonNode): JsonComparerContext {
        return JsonComparerContext(
                path.push(
                        JsonNodePathElement(parent, fieldName, fieldValue)
                )
        )
    }

    fun getCompareMode(): JsonCompareMode = CompareModeFinder.getCompareModeIncludingInheritance(path)

}