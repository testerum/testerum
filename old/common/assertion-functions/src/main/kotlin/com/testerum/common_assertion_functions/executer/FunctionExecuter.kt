package com.testerum.common_assertion_functions.executer

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.parser.ast.FunctionArgument

interface FunctionExecuter {

    /** @throws AssertionFailedException */
    fun execute(actualNode: JsonNode, arguments: List<FunctionArgument>)

    /**
     * The source from which this executer has been created, for use in error messages.
     * Can be for example, the class and method from which the executer was created.
     */
    val source: String

}