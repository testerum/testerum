package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.functions.AssertionFunction

object ObjectFunctions {

    @AssertionFunction
    fun isObject(actualNode: JsonNode) {
    }
    
    @AssertionFunction
    fun isObjectOrNull(actualNode: JsonNode) {
    }
}
