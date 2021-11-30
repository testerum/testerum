package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException

fun JsonNode.textValue(expectationMessage: String): String? = when (this) {
    is NullNode -> null
    is ValueNode -> this.asText()
    else -> throw AssertionFailedException("$expectationMessage, but got a node of type [${this.javaClass.simpleName}] instead")
}

fun verifyText(actualNode: JsonNode,
               expectationMessage: String,
               isCorrect: (actualValue: String?) -> Boolean) {
    val actualValue: String? = actualNode.textValue(expectationMessage)

    if (!isCorrect(actualValue)) {
        throw AssertionFailedException("$expectationMessage, but the actual value [$actualValue] does not match expectation")
    }
}
