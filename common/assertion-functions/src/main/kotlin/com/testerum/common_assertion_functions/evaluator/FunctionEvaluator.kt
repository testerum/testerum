package com.testerum.common_assertion_functions.evaluator

import com.fasterxml.jackson.databind.JsonNode
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuter
import com.testerum.common_assertion_functions.parser.FunctionCallParserFactory
import com.testerum.common_assertion_functions.parser.ast.FunctionCall

class FunctionEvaluator(private val delegatingFunctionExecuter: DelegatingFunctionExecuter) {

    private val PARSER_EXECUTER = ParserExecuter(
            FunctionCallParserFactory.functionCall()
    )

    fun evaluate(functionCallText: String, actualNode: JsonNode) {
        val functionCall: FunctionCall = PARSER_EXECUTER.parse(functionCallText)

        delegatingFunctionExecuter.execute(functionCall, actualNode)
    }

}