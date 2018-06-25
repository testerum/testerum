package com.testerum.test_file_format.common.step_call.`var`

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileStepVarParserTest {

    private val parser = ParserExecuter(
            FileStepVarParserFactory.stepVar()
    )

    @Test
    fun `should throw exception for missing var keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("= <<some variable value>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "= <<some variable value>>\n" +
                        "^--- ERROR at line 1, column 1: [var] expected, [=] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing variable name`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("var = <<some variable value>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "var = <<some variable value>>\n" +
                        "    ^--- ERROR at line 1, column 5: [variableName] expected, [=] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing =`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("var name <<some variable value>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "var name <<some variable value>>\n" +
                        "         ^--- ERROR at line 1, column 10: [=] expected, [<] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing body`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("var name = some variable value")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "var name = some variable value\n" +
                        "           ^--- ERROR at line 1, column 12: [verbatim or <<] expected, [s] encountered"
                )
        )
    }

    @Test
    fun `should parse empty value`() {
        assertThat(
                parser.parse("var name = <<>>"),
                equalTo(
                        FileStepVar(name = "name", value = "")
                )
        )
    }

    @Test
    fun `should parse single-line description`() {
        assertThat(
                parser.parse("var varName = <<some variable value>>"),
                equalTo(
                        FileStepVar(name = "varName", value = "some variable value")
                )
        )
    }

    @Test
    fun `should parse simple description`() {
        assertThat(
                parser.parse(
                        """ |var varName = <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |Some stuff
                                            |Other stuff""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `should parse simple description verbatim`() {
        assertThat(
                parser.parse(
                        """ |var varName = verbatim <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |
                                            |    Some stuff
                                            |    Other stuff
                                            |""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `should parse even if insignificant whitespace is missing`() {
        assertThat(
                parser.parse(
                        """ |var varName=<<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |Some stuff
                                            |Other stuff""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `should parse even if insignificant whitespace is missing, verbatim`() {
        assertThat(
                parser.parse(
                        """ |var varName=verbatim<<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |
                                            |    Some stuff
                                            |    Other stuff
                                            |""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `should ignore insignificant whitespace`() {
        assertThat(
                parser.parse(
                        """ |var  varName          =    <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |Some stuff
                                            |Other stuff""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `should ignore insignificant whitespace, verbatim`() {
        assertThat(
                parser.parse(
                        """ |var  varName             =      verbatim    <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |
                                            |    Some stuff
                                            |    Other stuff
                                            |""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, backslash`() {
        assertThat(
                parser.parse("""var varName = <<2 \ 1>>"""),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """2 \ 1"""
                        )
                )
        )
    }
    @Test
    fun `escaping - should parse single-line, one greaterThan, no escape needed`() {
        assertThat(
                parser.parse("""var varName = <<2 > 1>>"""),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """2 > 1"""
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, escaped double greaterThan`() {
        assertThat(
                parser.parse("""var varName = <<2 \>> 1>>"""),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """2 >> 1"""
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, backslash followed by double greaterThan`() {
        assertThat(
                parser.parse("""var varName = <<2 \\>> 1>>"""),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """2 \>> 1"""
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse single-line with multiple escapes`() {
        assertThat(
                parser.parse("""var varName = <<5000 \>> 200 \>> 1>>"""),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = "5000 >> 200 >> 1"
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line, backslash`() {
        assertThat(
                parser.parse(
                        """ |var varName = <<
                            |    2 \ 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |2 \ 1""".trimMargin()
                        )
                )
        )
    }
    @Test
    fun `escaping - should parse multi-line, one greaterThan, no escape needed`() {
        assertThat(
                parser.parse(
                        """ |var varName = <<
                            |    2 > 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |2 > 1""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line, escaped double greaterThan`() {
        assertThat(
                parser.parse(
                        """ |var varName = <<
                            |    2 \>> 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |2 >> 1""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line, backslash followed by double greaterThan`() {
        assertThat(
                parser.parse(
                        """ |var varName = <<
                            |    2 \\>> 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |2 \>> 1""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line with multiple escapes`() {
        assertThat(
                parser.parse(
                        """ |var varName = <<
                            |    5000 \>> 200
                            |    200 \>> 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepVar(
                                name = "varName",
                                value = """ |5000 >> 200
                                            |200 >> 1""".trimMargin()
                        )
                )
        )
    }

}