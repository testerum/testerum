package com.testerum.common_assertion_functions.parser

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.INTEGER
import com.testerum.common.parsing.util.CommonPatterns.LOWERCASE_LETTER
import com.testerum.common.parsing.util.CommonPatterns.UPPERCASE_LETTER
import com.testerum.common.parsing.util.CommonScanners.escapeSequence
import com.testerum.common_assertion_functions.parser.ast.*
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Pattern
import org.jparsec.pattern.Patterns.isChar
import org.jparsec.pattern.Patterns.many
import java.math.BigDecimal


object FunctionCallParserFactory : ParserFactory<FunctionCall> {

    private val FUNCTION_NAME_FIRST_CHARACTER: Pattern =
            isChar('_')
                    .or(LOWERCASE_LETTER)
                    .or(UPPERCASE_LETTER)

    private val FUNCTION_NAME_SUBSEQUENT_CHARACTER: Pattern =
            isChar('_')
                    .or(isChar('-'))
                    .or(LOWERCASE_LETTER)
                    .or(UPPERCASE_LETTER)

    private val FUNCTION_NAME_PATTERN: Pattern =
            FUNCTION_NAME_FIRST_CHARACTER
                    .next(FUNCTION_NAME_SUBSEQUENT_CHARACTER.many())

    private val NOT_SINGLE_QUOTE: Pattern =
            isChar { c -> c != '\'' }

    override fun createParser() = functionCall()

    fun functionCall(): Parser<FunctionCall> {
        return sequence(
                string("@"),
                functionName(),
                functionArgs()
        ) { _, functionName, argList -> FunctionCall(functionName, argList) }
    }

    private fun functionName(): Parser<String> {
        return FUNCTION_NAME_PATTERN.toScanner("functionName").source()
    }

    private fun functionArgs(): Parser<List<FunctionArgument>> {
        return sequence(
                string("("),
                functionArg()
                        .sepBy(
                                sequence(
                                        optionalWhitespace(),
                                        string(","),
                                        optionalWhitespace()
                                )
                        ),
                string(")")
        ) { _, argList, _ -> argList }
    }

    private fun functionArg(): Parser<FunctionArgument> {
        return or(
                nullFunctionArg(),
                booleanFunctionArg(),
                numberFunctionArg(),
                textFunctionArg()
        )
    }

    private fun nullFunctionArg(): Parser<NullFunctionArgument> {
        return string("null")
                .map { NullFunctionArgument }
    }

    private fun booleanFunctionArg(): Parser<BooleanFunctionArgument> {
        return or(
                string("false").source(),
                string("true").source()
        ).map { booleanValue -> BooleanFunctionArgument("true" == booleanValue) }
    }

    private fun numberFunctionArg(): Parser<NumberFunctionArgument> {
        return or(
                decimalNumber().map<DecimalFunctionArgument> { DecimalFunctionArgument(it) },
                integerNumber().map<IntegerFunctionArgument> { IntegerFunctionArgument(it) }
        )
    }

    private fun textFunctionArg(): Parser<TextFunctionArgument> {
        return sequence(
                string("'"),
                or(
                        escapeSequence(),
                        notSingleQuote()
                ).many(),
                string("'"),
                { _, texts, _ -> TextFunctionArgument(texts.joinToString(separator = "")) }
        )
    }

    private fun decimalNumber(): Parser<BigDecimal> {
        return sequence(
                integerNumberAsString(),
                sequence(
                        string(".").source(),
                        integerNumberAsString()
                ) { dot, integer -> dot + integer }
        ) { integerPart, decimalPart -> BigDecimal(integerPart + decimalPart) }
    }

    private fun integerNumber(): Parser<Int> {
        return INTEGER.toScanner("integer")
                .source()
                .map { Integer.parseInt(it) }
    }

    private fun integerNumberAsString(): Parser<String> {
        return INTEGER.toScanner("integer")
                .source()
    }

    private fun notSingleQuote(): Parser<String> {
        return NOT_SINGLE_QUOTE.toScanner("notSingleQuote")
                .source()
    }

    private fun optionalWhitespace(): Parser<Void> {
        return many(CharPredicates.IS_WHITESPACE)
                .toScanner("optional-whitespace")
    }

}
