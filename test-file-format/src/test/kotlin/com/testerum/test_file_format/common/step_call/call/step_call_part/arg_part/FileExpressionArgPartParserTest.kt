package com.testerum.test_file_format.common.step_call.call.step_call_part.arg_part

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPartParserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileExpressionArgPartParserTest {

    private val parser = ParserExecuter(
        FileExpressionArgPartParserFactory.expressionArgPart()
    )

    @Test
    fun `should throw exception for missing open curly brackets`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("varName}}")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "varName}}\n" +
                    "^--- ERROR at line 1, column 1: [{{] expected, [v] encountered"
            )
    }

    @Test
    fun `should throw exception for missing closing curly brackets`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("{{varName")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "{{varName\n" +
                    "         ^--- ERROR at line 1, column 10: [\\}}, notClosingCurlyBrackets or }}] expected, [EOF] encountered"
            )
    }

    @Test
    fun `should parse expressionArgPart`() {
        assertThat(parser.parse("{{varName}}"))
            .isEqualTo(
                FileExpressionArgPart("varName")
            )
    }

    @Test
    fun `should not ignore whitespace around expressionArgPart`() {
        assertThat(parser.parse("{{   varName         }}"))
            .isEqualTo(
                FileExpressionArgPart("   varName         ")
            )
    }

}
