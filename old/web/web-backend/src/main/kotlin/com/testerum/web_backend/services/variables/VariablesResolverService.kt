package com.testerum.web_backend.services.variables

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPart

class VariablesResolverService {

    companion object {
        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())
    }

    fun resolve(textToResolve: String,
                variables: Map<String, String>): String {
        val resolvedArgParts = mutableListOf<Any?>()

        val parts = ARG_PART_PARSER.parse(textToResolve)

        for (part: FileArgPart in parts) {
            val resolvedArgPartPart: Any? = when (part) {
                is FileTextArgPart -> part.text
                is FileExpressionArgPart -> {
                    try {
                        ExpressionEvaluator.evaluate(part.text, variables)
                    } catch (e: Exception) {
                        part.text
                    }
                }
            }

            resolvedArgParts += resolvedArgPartPart
        }

        return resolvedArgParts.joinToString(separator = "")
    }

}
