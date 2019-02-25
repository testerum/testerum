package com.testerum.test_file_format.common.util

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.util.CommonScanners.escapeSequence
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.isChar
import org.jparsec.Scanners.string
import org.jparsec.pattern.Patterns

object TestFileFormatScanners {

    private val variableNameParser: ParserExecuter<String> = ParserExecuter(variableName())

    fun isValidVariableName(text: String): Boolean = variableNameParser.isValid(text)

    fun variableName(): Parser<String> = Patterns.WORD.toScanner("variableName").source()

    fun javaIdentifier(): Parser<String> = Patterns.WORD.toScanner("javaIdentifier").source()

    fun variableType(): Parser<String> = sequence(
            javaIdentifier(),
            sequence(
                    isChar('.').source(),
                    javaIdentifier(),
                    { first, second -> first + second }
            ).many().map { it.joinToString(separator = "") },
            { first, second -> first + second }
    )

    fun multiLineAngleText(): Parser<String> {
        return sequence(
                verbatim(),
                string("<<"),
                or(
                        escapeSequence(">>"),
                        notClosingAngleBrackets()
                ).many(),
                string(">>")
        ) { verbatim, _, texts, _ ->
            val joinedTexts = texts.joinToString(separator = "")

            if (verbatim) {
                joinedTexts
            } else {
                joinedTexts.trimIndent()
            }
        }
    }

    private fun notClosingAngleBrackets(): Parser<String> {
        return Patterns.notString(">>").toScanner("notClosingAngleBrackets")
                .source()
    }

    private fun verbatim(): Parser<Boolean> {
        return sequence(
                string("verbatim"),
                optionalWhitespace()
        ) { _, _ -> true }
                .optional(false)
    }
}
