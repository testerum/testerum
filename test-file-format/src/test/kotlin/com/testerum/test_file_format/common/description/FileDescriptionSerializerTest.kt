package com.testerum.test_file_format.common.description

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Test

class FileDescriptionSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileDescriptionSerializer,
            FileDescriptionParserFactory.description()
    )

    @Test
    fun empty() {
        testRunner.execute(
                original = "",
                indentLevel = 0,
                expected = """|description = <<>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `single line`() {
        testRunner.execute(
                original = "a simple description",
                indentLevel = 0,
                expected = """|description = <<a simple description>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `multi line`() {
        testRunner.execute(
                original = """ |First line
                               |Second line
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |    Second line
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

    @Test
    fun `indented multi line`() {
        testRunner.execute(
                original = """ |First line
                               |    1.1
                               |    1.2
                               |Second line
                               |    2.1
                               |        2.1.1
                               |        2.1.2
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |        1.1
                               |        1.2
                               |    Second line
                               |        2.1
                               |            2.1.1
                               |            2.1.2
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

    @Test
    fun `escaping - single line, backslash`() {
        testRunner.execute(
                original = """2 \ 1""",
                indentLevel = 0,
                expected = """|description = <<2 \ 1>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `escaping - single line, one greaterThan, no escape needed`() {
        testRunner.execute(
                original = """2 > 1""",
                indentLevel = 0,
                expected = """|description = <<2 \> 1>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `escaping - single line, one greaterThan, escaped double greaterThan`() {
        testRunner.execute(
                original = """2 >> 1""",
                indentLevel = 0,
                expected = """|description = <<2 \>\> 1>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `escaping - single line, one greaterThan, backslash followed by double greaterThan`() {
        testRunner.execute(
                original = """2 \>> 1""",
                indentLevel = 0,
                expected = """|description = <<2 \\>\> 1>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `escaping - single line, one greaterThan, multiple escapes`() {
        testRunner.execute(
                original = """5000 >> 200 >> 1""",
                indentLevel = 0,
                expected = """|description = <<5000 \>\> 200 \>\> 1>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `escaping - multi line, backslash`() {
        testRunner.execute(
                original = """ |First line
                               |2 \ 1
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |    2 \ 1
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

    @Test
    fun `escaping - multi line, one greaterThan, no escape needed`() {
        testRunner.execute(
                original = """ |First line
                               |2 > 1
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |    2 \> 1
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

    @Test
    fun `escaping - multi line, escaped double greaterThan`() {
        testRunner.execute(
                original = """ |First line
                               |2 >> 1
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |    2 \>\> 1
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

    @Test
    fun `escaping - multi line, backslash followed by double greaterThan`() {
        testRunner.execute(
                original = """ |First line
                               |2 \>> 1
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |    2 \\>\> 1
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

    @Test
    fun `escaping - multi line, multiple escapes`() {
        testRunner.execute(
                original = """ |First line
                               |5000 >> 200
                               |200 >> 1
                               |Another line""".trimMargin(),
                indentLevel = 0,
                expected = """ |description = <<
                               |    First line
                               |    5000 \>\> 200
                               |    200 \>\> 1
                               |    Another line
                               |>>
                               |""".trimMargin()

        )
    }

}
