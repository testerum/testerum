package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.functions.AssertionFunction

object ArrayFunctions {

    @AssertionFunction
    fun isArray(actualNode: JsonNode) {
    }
    
    @AssertionFunction
    fun isArrayOrNull(actualNode: JsonNode) {
    }

    @AssertionFunction
    fun isNonEmptyArray(actualNode: JsonNode) {
    }
    @AssertionFunction
    fun isArrayWithExactNumberOfElements(actualNode: JsonNode) {
    }
    @AssertionFunction
    fun isArrayWithMoreElementsThen(actualNode: JsonNode) {
    }
    @AssertionFunction
    fun isArrayWithLessElementsThen(actualNode: JsonNode) {
    }
}
