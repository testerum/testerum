package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction

object BooleanFunctions {

    @AssertionFunction
    fun isBoolean(actualNode: JsonNode) {
        if (actualNode is BooleanNode) {
            return
        }

        throw AssertionFailedException("expected an Boolean, but got ${actualNode.toString()}")
    }

    @AssertionFunction
    fun isBooleanOrNull(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }

        if (actualNode is BooleanNode) {
            return
        }

        throw AssertionFailedException("expected an Boolean, but got ${actualNode.toString()}")
    }
}
