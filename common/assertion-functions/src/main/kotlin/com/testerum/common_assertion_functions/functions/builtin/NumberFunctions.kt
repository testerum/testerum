package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.functions.AssertionFunction

@Suppress("UNUSED_PARAMETER")
object NumberFunctions {

    //TODO: set separator optionally
    @AssertionFunction
    fun isNumber(actualNode: JsonNode) {
    }

    @AssertionFunction
    fun isInteger(actualNode: JsonNode) {
    }

    @AssertionFunction
    fun isNumberLessThen(actualNode: JsonNode) {
    }

    @AssertionFunction
    fun isNumberEqualOrLessThen(actualNode: JsonNode) {
    }

    @AssertionFunction
    fun isNumberBiggerThen(actualNode: JsonNode) {
    }

    @AssertionFunction
    fun isNumberEqualOrBiggerThen(actualNode: JsonNode) {
    }
}
