package com.testerum.test_file_format.common.step_call.part.arg_part

import com.testerum.common.parsing.ParserFactory
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import org.jparsec.pattern.Patterns

object FileExpressionArgPartParserFactory : ParserFactory<FileExpressionArgPart> {


    override fun createParser() = expressionArgPart()

    fun expressionArgPart(): Parser<FileExpressionArgPart> {
        return sequence(
                string("{{"),
                notClosingCurlyBrackets().many(),
                string("}}")
        ) { _, texts, _ -> FileExpressionArgPart(texts.joinToString(separator = "")) }
    }

    // note: for now, we don't support escapes here, because the expression can only be a variable name
    private fun notClosingCurlyBrackets(): Parser<String> {
        return Patterns.notString("}}")
                .toScanner("notClosingCurlyBrackets")
                .source()
    }

}