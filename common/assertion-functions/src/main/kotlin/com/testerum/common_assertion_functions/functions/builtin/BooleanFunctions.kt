package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.functions.AssertionFunction

object BooleanFunctions {

    @AssertionFunction
    fun isBoolean(actualNode: JsonNode) {
    }
    
    @AssertionFunction
    fun isBooleanOrNull(actualNode: JsonNode) {
    }
}
