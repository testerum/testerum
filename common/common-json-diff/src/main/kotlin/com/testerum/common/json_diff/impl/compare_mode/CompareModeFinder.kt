package com.testerum.common.json_diff.impl.compare_mode

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.testerum.common.json_diff.json_path.JsonArrayItemPathElement
import com.testerum.common.json_diff.json_path.JsonNodePathElement
import com.testerum.common.json_diff.json_path.JsonPath
import com.testerum.common.json_diff.json_path.JsonPathElement

object CompareModeFinder {

    val OBJECT_COMPARE_MODE_FIELD_NAME = "=compareMode"
    private val ARRAY_COMPARE_MODE_ITEM_START = "=compareMode:"

    private val DEFAULT_COMPARE_MODE = JsonCompareMode.CONTAINS


    fun getCompareModeIncludingInheritance(path: JsonPath): JsonCompareMode {
        val elementsFromLeaf = path.elementsFromRoot.asReversed()

        for (pathElement in elementsFromLeaf) {
            val compareMode = getCompareModeForPathElement(pathElement, path)
            if (compareMode != null) {
                return compareMode
            }
        }

        return DEFAULT_COMPARE_MODE
    }

    fun getCompareModeForPathElement(pathElement: JsonPathElement<*>, path: JsonPath): JsonCompareMode? = when (pathElement) {
        is JsonArrayItemPathElement -> getCompareModeForArray(pathElement.parent, path)
        is JsonNodePathElement -> getCompareModeForObject(pathElement.node, path)
    }

    fun getCompareModeForArray(arrayNode: ArrayNode, path: JsonPath): JsonCompareMode? {
        val firstItem: JsonNode = arrayNode[0]
                ?: return null

        if (firstItem !is TextNode) {
            return null
        }

        val firstItemTextValue = firstItem.textValue()
        if (!firstItemTextValue.startsWith(ARRAY_COMPARE_MODE_ITEM_START)) {
            return null
        }

        try {
            return JsonCompareMode.parse(
                    firstItemTextValue.substring(ARRAY_COMPARE_MODE_ITEM_START.length)
                            .trim()
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("${e.message}; error found at $path")
        }
    }

    fun getCompareModeForObject(objectNode: JsonNode, path: JsonPath): JsonCompareMode? {
        if (objectNode !is ObjectNode) {
            return null
        }
        val compareModeNode = objectNode[OBJECT_COMPARE_MODE_FIELD_NAME]
                ?: return null

        if (compareModeNode !is TextNode) {
            throw IllegalArgumentException(
                    "compare mode value must be a String, but found [${compareModeNode.javaClass.name}] instead" +
                            "; error found at $path"
            )
        }

        try {
            return JsonCompareMode.parse(
                    compareModeNode.textValue()
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("${e.message}; error found at $path")
        }
    }

}