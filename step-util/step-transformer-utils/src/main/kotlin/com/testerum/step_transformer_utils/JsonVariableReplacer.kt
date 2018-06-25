package com.testerum.step_transformer_utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.testerum.api.test_context.test_vars.TestVariables

class JsonVariableReplacer(private val testVariables: TestVariables) {

    // todo: remove this class and make it easier for custom transformers to do this (make this part of TestVariables? make it a separate module, to not depend on Jackson?)

    fun replaceVariables(node: JsonNode) {
        when (node) {
            is ArrayNode -> {
                for (elementNode in node.elements()) {
                    replaceVariables(elementNode)
                }
            }
            is ObjectNode -> {
                for (fieldName in node.fieldNames()) {
                    val fieldValue: JsonNode = node.get(fieldName)

                    if (fieldValue is TextNode) {
                        val textValue = fieldValue.textValue()
                        if (textValue != null) {
                            val resolvedValue = testVariables.resolveIn(textValue)

                            node.replace(fieldName, TextNode(resolvedValue))
                        }
                    } else {
                        replaceVariables(fieldValue)
                    }
                }
            }
        }
    }

}