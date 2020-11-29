package com.testerum.test_file_format.manual_test.comments

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileManualCommentsParserTest {

    private val parser = ParserExecuter(
        FileManualCommentsParserFactory.manualTestComments()
    )

    @Test
    fun `should throw exception for missing description keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("= <<some description>>")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "= <<some description>>\n" +
                    "^--- ERROR at line 1, column 1: [comments] expected, [=] encountered"
            )
    }

    @Test
    fun `should throw exception for missing =`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("comments <<some comments>>")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "comments <<some comments>>\n" +
                    "         ^--- ERROR at line 1, column 10: [=] expected, [<] encountered"
            )
    }

    @Test
    fun `should throw exception for missing body`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("comments = some comments")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "comments = some comments\n" +
                    "           ^--- ERROR at line 1, column 12: [verbatim or <<] expected, [s] encountered"
            )
    }

    @Test
    fun `should parse empty description`() {
        assertThat(parser.parse("comments = <<>>"))
            .isEqualTo("")
    }

    @Test
    fun `should parse single-line description`() {
        assertThat(parser.parse("comments = <<some comments>>"))
            .isEqualTo("some comments")
    }

    @Test
    fun `should parse simple description`() {
        assertThat(
            parser.parse(
                """ |comments = <<
                    |    Some stuff
                    |    Other stuff
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |Some stuff
                |Other stuff""".trimMargin()
        )
    }

    @Test
    fun `should parse simple description, verbatim`() {
        assertThat(
            parser.parse(
                """ |comments = verbatim <<
                    |    Some stuff
                    |    Other stuff
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |
                |    Some stuff
                |    Other stuff
                |""".trimMargin()
        )
    }

    @Test
    fun `should parse even if insignificant whitespace is missing`() {
        assertThat(
            parser.parse(
                """ |comments=<<
                    |    Some stuff
                    |    Other stuff
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |Some stuff
                |Other stuff""".trimMargin()
        )
    }

    @Test
    fun `should parse even if insignificant whitespace is missing, verbatim`() {
        assertThat(
            parser.parse(
                """ |comments=verbatim<<
                    |    Some stuff
                    |    Other stuff
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |
                |    Some stuff
                |    Other stuff
                |""".trimMargin()
        )
    }

    @Test
    fun `should ignore insignificant whitespace`() {
        assertThat(
            parser.parse(
                """ |comments  =    <<
                    |    Some stuff
                    |    Other stuff
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |Some stuff
                |Other stuff""".trimMargin()
        )
    }

    @Test
    fun `should ignore insignificant whitespace, verbatim`() {
        assertThat(
            parser.parse(
                """ |comments  =     verbatim           <<
                    |    Some stuff
                    |    Other stuff
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |
                |    Some stuff
                |    Other stuff
                |""".trimMargin()
        )
    }

    @Test
    fun `escaping - should parse single-line, backslash`() {
        assertThat(parser.parse("""comments = <<2 \ 1>>"""))
            .isEqualTo("""2 \ 1""")
    }

    @Test
    fun `escaping - should parse single-line, one greaterThan, no escape needed`() {
        assertThat(parser.parse("""comments = <<2 > 1>>"""))
            .isEqualTo("""2 > 1""")
    }

    @Test
    fun `escaping - should parse single-line, greaterThan`() {
        assertThat(parser.parse("""comments = <<2 > 1>>"""))
            .isEqualTo("""2 > 1""")
    }

    @Test
    fun `escaping - should parse single-line, escaped double greaterThan`() {
        assertThat(parser.parse("""comments = <<2 \>> 1>>"""))
            .isEqualTo("""2 >> 1""")
    }

    @Test
    fun `escaping - should parse single-line, backslash followed by double greaterThan`() {
        assertThat(parser.parse("""comments = <<2 \\>> 1>>"""))
            .isEqualTo("""2 \>> 1""")
    }

    @Test
    fun `escaping - should parse single-line with multiple escapes`() {
        assertThat(parser.parse("""comments = <<5000 \>> 200 \>> 1>>"""))
            .isEqualTo("5000 >> 200 >> 1")
    }

    @Test
    fun `escaping - should parse multi-line, backslash`() {
        assertThat(
            parser.parse(
                """ |comments = <<
                    |    2 \ 1
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |2 \ 1""".trimMargin()
        )
    }

    @Test
    fun `escaping - should parse multi-line, one greaterThan, no escape needed`() {
        assertThat(
            parser.parse(
                """ |comments = <<
                    |    2 > 1
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |2 > 1""".trimMargin()
        )
    }

    @Test
    fun `escaping - should parse multi-line, escaped double greaterThan`() {
        assertThat(
            parser.parse(
                """ |comments = <<
                    |    2 \>> 1
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |2 >> 1""".trimMargin()
        )
    }

    @Test
    fun `escaping - should parse multi-line, backslash followed by double greaterThan`() {
        assertThat(
            parser.parse(
                """ |comments = <<
                    |    2 \\>> 1
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |2 \>> 1""".trimMargin()
        )
    }

    @Test
    fun `escaping - should parse multi-line with multiple escapes`() {
        assertThat(
            parser.parse(
                """ |comments = <<
                    |    5000 \>> 200
                    |    200 \>> 1
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            """ |5000 >> 200
                |200 >> 1""".trimMargin()
        )
    }

}
