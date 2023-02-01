package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction

object ObjectFunctions {

    @AssertionFunction
    fun isObject(actualNode: JsonNode) {
        if (actualNode is ObjectNode) {
            return
        }

        throw AssertionFailedException("expected an Object, but got ${actualNode.toString()}")
    }
    
    @AssertionFunction
    fun isObjectOrNull(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }

        if (actualNode is ObjectNode) {
            return
        }

        throw AssertionFailedException("expected an Object, but got ${actualNode.toString()}")
    }
}
