package com.testerum.common_assertion_functions.executer

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.parser.ast.FunctionCall
import com.testerum.common_assertion_functions.utils.didYouMean

class DelegatingFunctionExecuter(private val executers: Map<String /*functionName*/, FunctionExecuter>) {

    /** @throws AssertionFailedException */
    fun execute(functionCall: FunctionCall, actualNode: JsonNode) {
        val executer: FunctionExecuter? = executers[functionCall.functionName]

        if (executer == null) {
            val didYouMean: String = didYouMean(functionCall.functionName, executers.keys)
            throw IllegalArgumentException("unknown function [${functionCall.functionName}]${didYouMean}")
        }

        executer.execute(actualNode, functionCall.functionArgs)
    }

}