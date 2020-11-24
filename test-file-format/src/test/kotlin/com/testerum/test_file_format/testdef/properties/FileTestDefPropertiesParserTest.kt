package com.testerum.test_file_format.testdef.properties

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileTestDefPropertiesParserTest {

    private val parser: ParserExecuter<FileTestDefProperties> = ParserExecuter(
        FileTestDefPropertiesParserFactory.testProperties()
    )

    @Test
    fun `should throw exception for missing test-properties keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("= <<manual>>")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "= <<manual>>\n" +
                    "^--- ERROR at line 1, column 1: [test-properties] expected, [=] encountered"
            )
    }

    @Test
    fun `should throw exception for missing =`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("test-properties <<manual>>")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "test-properties <<manual>>\n" +
                    "                ^--- ERROR at line 1, column 17: [=] expected, [<] encountered"
            )
    }

    @Test
    fun `should throw exception for missing body`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("test-properties = manual")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "test-properties = manual\n" +
                    "                  ^--- ERROR at line 1, column 19: [<<] expected, [m] encountered"
            )
    }

    @Test
    fun `should parse empty list of properties - single line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = false, isDisabled = false)
        )
    }

    @Test
    fun `should parse empty list of properties - multi-line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<
                    |
                    |
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = false, isDisabled = false)
        )
    }

    @Test
    fun `should parse manual - single line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<manual>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = true, isDisabled = false)
        )
    }

    @Test
    fun `should parse manual - multi-line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<
                    |
                    |manual
                    |
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = true, isDisabled = false)
        )
    }

    @Test
    fun `should parse disabled - single line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<disabled>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = false, isDisabled = true)
        )
    }

    @Test
    fun `should parse disabled - multi-line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<
                    |
                    |disabled
                    |
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = false, isDisabled = true)
        )
    }

    @Test
    fun `should parse both manual and disabled - single line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<manual, disabled>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = true, isDisabled = true)
        )
    }

    @Test
    fun `should parse both manual and disabled - multi-line`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<
                    |    manual
                    |    ,
                    |
                    |    disabled
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = true, isDisabled = true)
        )
    }

    @Test
    fun `should parse both manual and disabled - single line, reverse order`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<disabled, manual>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = true, isDisabled = true)
        )
    }

    @Test
    fun `should parse both manual and disabled - multi-line, reverse order`() {
        assertThat(
            parser.parse(
                """ |test-properties = <<
                    |    disabled
                    |    ,
                    |
                    |    manual
                    |>>
                """.trimMargin()
            )
        ).isEqualTo(
            FileTestDefProperties(isManual = true, isDisabled = true)
        )
    }

}
