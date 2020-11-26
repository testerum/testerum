package com.testerum.test_file_format.common.step_call.part.arg_part

import com.testerum.common.parsing.executer.ParserExecuter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileArgPartParserFactoryTest {

    private val parser = ParserExecuter(
        FileArgPartParserFactory.argParts()
    )

    @Test
    fun `mixed text and expressions`() {
        assertThat(parser.parse("text 1 {{expr1}} text 2 {{expr2}} text 3"))
            .isEqualTo(
                listOf(
                    FileTextArgPart("text 1 "),
                    FileExpressionArgPart("expr1"),
                    FileTextArgPart(" text 2 "),
                    FileExpressionArgPart("expr2"),
                    FileTextArgPart(" text 3")
                )
            )
    }

    @Test
    fun `text with escaped expression start`() {
        assertThat(parser.parse("""left \{{ right"""))
            .isEqualTo(
                listOf<FileArgPart>(
                    FileTextArgPart("left {{ right")
                )
            )
    }

    @Test
    fun `expression with escaped expression end`() {
        assertThat(parser.parse("""{{left \}} right}}"""))
            .isEqualTo(
                listOf<FileArgPart>(
                    FileExpressionArgPart("left }} right")
                )
            )
    }

}
