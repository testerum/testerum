package com.testerum.test_file_format.common.tags

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileTagsParserTest {

    private val parser: ParserExecuter<List<String>> = ParserExecuter(
            FileTagsParserFactory.tags()
    )

    @Test
    fun `should throw exception for missing "tags" keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("= <<tag>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "= <<tag>>\n" +
                        "^--- ERROR at line 1, column 1: [tags] expected, [=] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing =`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("tags <<tag>>")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "tags <<tag>>\n" +
                        "     ^--- ERROR at line 1, column 6: [=] expected, [<] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing body`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("tags = tag")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "tags = tag\n" +
                        "       ^--- ERROR at line 1, column 8: [<<] expected, [t] encountered"
                )
        )
    }

    @Test
    fun `should parse empty list of tags - single line`() {
        assertThat(
                parser.parse(
                        """ |tags = <<>>
                        """.trimMargin()
                ),
                equalTo(
                        listOf()
                )
        )
    }

    @Test
    fun `should parse empty list of tags - multi-line`() {
        assertThat(
                parser.parse(
                        """ |tags = <<
                            |
                            |
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        listOf()
                )
        )
    }

    @Test
    fun `should parse one tag - single line`() {
        assertThat(
                parser.parse(
                        """ |tags = <<tag>>
                        """.trimMargin()
                ),
                equalTo(
                        listOf("tag")
                )
        )
    }

    @Test
    fun `should parse one tag - multi-line`() {
        assertThat(
                parser.parse(
                        """ |tags = <<
                            |
                            |tag
                            |
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        listOf("tag")
                )
        )
    }

    @Test
    fun `should parse multiple tags - single line`() {
        assertThat(
                parser.parse(
                        """ |tags = <<one,two,three>>
                        """.trimMargin()
                ),
                equalTo(
                        listOf("one", "two", "three")
                )
        )
    }

    @Test
    fun `should parse multiple tags - multi-line`() {
        assertThat(
                parser.parse(
                        """ |tags = <<
                            |    one,
                            |    two,
                            |    three
                            |    ,
                            |
                            |    four
                            |    ,five
                            |>>
                        """.trimMargin()
                ),
                equalTo(
                        listOf("one", "two", "three", "four", "five")
                )
        )
    }

}