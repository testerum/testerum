package com.testerum.test_file_format.common.description

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileDescriptionParserTest {

    private val parser = ParserExecuter(
            FileDescriptionParserFactory.description()
    )

    @Test
    fun `should throw exception for missing "description" keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("= <<some description>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "= <<some description>>\n" +
                        "^--- ERROR at line 1, column 1: [description] expected, [=] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing =`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("description <<some description>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "description <<some description>>\n" +
                        "            ^--- ERROR at line 1, column 13: [=] expected, [<] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing body`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("description = some description")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "description = some description\n" +
                        "              ^--- ERROR at line 1, column 15: [verbatim or <<] expected, [s] encountered"
                )
        )
    }

    @Test
    fun `should parse empty description`() {
        assertThat(
                parser.parse("description = <<>>"),
                equalTo(
                        ""
                )
        )
    }

    @Test
    fun `should parse single-line description`() {
        assertThat(
                parser.parse("description = <<some description>>"),
                equalTo(
                        "some description"
                )
        )
    }

    @Test
    fun `should parse simple description`() {
        assertThat(
                parser.parse(
                        """ |description = <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |Some stuff
                            |Other stuff""".trimMargin()
                )
        )
    }

    @Test
    fun `should parse simple description, verbatim`() {
        assertThat(
                parser.parse(
                        """ |description = verbatim <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |
                            |    Some stuff
                            |    Other stuff
                            |""".trimMargin()
                )
        )
    }

    @Test
    fun `should parse even if insignificant whitespace is missing`() {
        assertThat(
                parser.parse(
                        """ |description=<<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |Some stuff
                            |Other stuff""".trimMargin()
                )
        )
    }

    @Test
    fun `should parse even if insignificant whitespace is missing, verbatim`() {
        assertThat(
                parser.parse(
                        """ |description=verbatim<<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |
                            |    Some stuff
                            |    Other stuff
                            |""".trimMargin()
                )
        )
    }

    @Test
    fun `should ignore insignificant whitespace`() {
        assertThat(
                parser.parse(
                        """ |description  =    <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |Some stuff
                            |Other stuff""".trimMargin()
                )
        )
    }

    @Test
    fun `should ignore insignificant whitespace, verbatim`() {
        assertThat(
                parser.parse(
                        """ |description  =     verbatim           <<
                            |    Some stuff
                            |    Other stuff
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |
                            |    Some stuff
                            |    Other stuff
                            |""".trimMargin()
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, backslash`() {
        assertThat(
                parser.parse("""description = <<2 \ 1>>"""),
                equalTo(
                        """2 \ 1"""
                )
        )
    }
    @Test
    fun `escaping - should parse single-line, one greaterThan, no escape needed`() {
        assertThat(
                parser.parse("""description = <<2 > 1>>"""),
                equalTo(
                        """2 > 1"""
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, greaterThan`() {
        assertThat(
                parser.parse("""description = <<2 > 1>>"""),
                equalTo(
                        """2 > 1"""
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, escaped double greaterThan`() {
        assertThat(
                parser.parse("""description = <<2 \>> 1>>"""),
                equalTo(
                        """2 >> 1"""
                )
        )
    }

    @Test
    fun `escaping - should parse single-line, backslash followed by double greaterThan`() {
        assertThat(
                parser.parse("""description = <<2 \\>> 1>>"""),
                equalTo(
                        """2 \>> 1"""
                )
        )
    }

    @Test
    fun `escaping - should parse single-line with multiple escapes`() {
        assertThat(
                parser.parse("""description = <<5000 \>> 200 \>> 1>>"""),
                equalTo(
                        "5000 >> 200 >> 1"
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line, backslash`() {
        assertThat(
                parser.parse(
                        """ |description = <<
                            |    2 \ 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |2 \ 1""".trimMargin()
                )
        )
    }
    @Test
    fun `escaping - should parse multi-line, one greaterThan, no escape needed`() {
        assertThat(
                parser.parse(
                        """ |description = <<
                            |    2 > 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |2 > 1""".trimMargin()
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line, escaped double greaterThan`() {
        assertThat(
                parser.parse(
                        """ |description = <<
                            |    2 \>> 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |2 >> 1""".trimMargin()
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line, backslash followed by double greaterThan`() {
        assertThat(
                parser.parse(
                        """ |description = <<
                            |    2 \\>> 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |2 \>> 1""".trimMargin()
                )
        )
    }

    @Test
    fun `escaping - should parse multi-line with multiple escapes`() {
        assertThat(
                parser.parse(
                        """ |description = <<
                            |    5000 \>> 200
                            |    200 \>> 1
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        """ |5000 >> 200
                            |200 >> 1""".trimMargin()
                )
        )
    }

}