package com.testerum.common.json_diff.impl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.context.JsonComparerContext
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonNodeComparer

class JsonComparerImpl(private val jsonNodeComparer: JsonNodeComparer) : JsonComparer {

    companion object {
        private val OBJECT_MAPPER = ObjectMapper().apply {
            // useful for test data
            enable(JsonParser.Feature.ALLOW_COMMENTS)

            // because float/double is lossy
            enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        }
    }

    override fun compare(expectedJson: String, actualJson: String): JsonCompareResult {
        val expectedRoot: JsonNode = OBJECT_MAPPER.readTree(expectedJson)
                ?: throw IllegalArgumentException("invalid expected JSON: empty text")
        val actualRoot: JsonNode = OBJECT_MAPPER.readTree(actualJson)
                ?: throw IllegalArgumentException("invalid actual JSON: empty text")

        val context = JsonComparerContext.EMPTY
                .pushNonArrayItem(parent = null, fieldName = "", fieldValue = expectedRoot)

        return jsonNodeComparer.compareNodes(expectedRoot, actualRoot, context)
    }

}
