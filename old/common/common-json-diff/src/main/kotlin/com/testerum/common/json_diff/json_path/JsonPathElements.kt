package com.testerum.common.json_diff.json_path

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

sealed class JsonPathElement<out P: JsonNode?>(val parent: P)
class JsonNodePathElement(parent: ObjectNode?, val fieldName: String, val node: JsonNode) : JsonPathElement<ObjectNode?>(parent)
class JsonArrayItemPathElement(parent: ArrayNode, val index: Int) : JsonPathElement<ArrayNode>(parent)
