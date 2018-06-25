package com.testerum.common_assertion_functions.parser

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import com.testerum.common_assertion_functions.parser.ast.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FunctionCallParserTest {

    private val parser = ParserExecuter(
            FunctionCallParserFactory.functionCall()
    )

    @Test
    fun `should fail if the text doesn't start with @`() {
        Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("isNotNull()")
        }
    }

    @Test
    fun `should fail with good error message`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("@is/not/null")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "@is/not/null\n" +
                        "   ^--- ERROR at line 1, column 4: [(] expected, [/] encountered"
                )
        )
    }

    @Test
    fun `should parse calls without parameters`() {
        assertEquals(
                parser.parse("@isNotNull()"),
                FunctionCall(functionName = "isNotNull")
        )
    }

    @Test
    fun `should parse calls with one null argument`() {
        assertEquals(
                parser.parse("@isEqualsTo(null)"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(NullFunctionArgument)
                )
        )
    }

    @Test
    fun `should parse calls with one false boolean argument`() {
        assertEquals(
                parser.parse("@isEqualsTo(false)"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(BooleanFunctionArgument(false))
                )
        )
    }

    @Test
    fun `should parse calls with one true boolean argument`() {
        assertEquals(
                parser.parse("@isEqualsTo(true)"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(BooleanFunctionArgument(true))
                )
        )
    }

    @Test
    fun `should parse calls with one integer argument`() {
        assertEquals(
                parser.parse("@isGreaterThan(13)"),
                FunctionCall(
                        functionName = "isGreaterThan",
                        functionArgs = listOf(IntegerFunctionArgument(13))
                )
        )
    }

    @Test
    fun `should parse calls with one decimal argument`() {
        assertEquals(
                parser.parse("@isGreaterThan(13.57)"),
                FunctionCall(
                        functionName = "isGreaterThan",
                        functionArgs = listOf(DecimalFunctionArgument("13.57"))
                )
        )
    }

    @Test
    fun `should parse calls with one empty text argument`() {
        assertEquals(
                parser.parse("@isEqualsTo('')"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(TextFunctionArgument(""))
                )
        )
    }

    @Test
    fun `should parse calls with one single letter text argument`() {
        assertEquals(
                parser.parse("@isEqualsTo('a')"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(TextFunctionArgument("a"))
                )
        )
    }

    @Test
    fun `should parse calls with one single digit text argument`() {
        assertEquals(
                parser.parse("@isEqualsTo('1')"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(TextFunctionArgument("1"))
                )
        )
    }

    @Test
    fun `should parse calls with one multiple-characters text argument`() {
        assertEquals(
                parser.parse("@isEqualsTo('Hello, my name is John Doe and I am 21 years old.')"),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(TextFunctionArgument("Hello, my name is John Doe and I am 21 years old."))
                )
        )
    }

    @Test
    fun `should parse calls with one text argument with escape sequences`() {
        assertEquals(
                parser.parse("""@isEqualsTo('c:\\Users\\John McDonald\'s\\Documents')"""),
                FunctionCall(
                        functionName = "isEqualsTo",
                        functionArgs = listOf(TextFunctionArgument("""c:\Users\John McDonald's\Documents"""))
                )
        )
    }

    @Test
    fun `should parse calls with multiple arguments with mixed types`() {
        assertEquals(
                parser.parse("""@aFunction('', 'a', 'word', '  more words with 2 leading spaces and 3 trailing spaces   ', 'McDonald\'s', 'C:\\Users', false, 13, true, null, false, 15.23)"""),
                FunctionCall(
                        functionName = "aFunction",
                        functionArgs = listOf(
                                TextFunctionArgument(""),
                                TextFunctionArgument("a"),
                                TextFunctionArgument("word"),
                                TextFunctionArgument("  more words with 2 leading spaces and 3 trailing spaces   "),
                                TextFunctionArgument("McDonald's"),
                                TextFunctionArgument("""C:\Users"""),
                                BooleanFunctionArgument(false),
                                IntegerFunctionArgument(13),
                                BooleanFunctionArgument(true),
                                NullFunctionArgument,
                                BooleanFunctionArgument(false),
                                DecimalFunctionArgument("15.23")
                        )
                )
        )
    }

}