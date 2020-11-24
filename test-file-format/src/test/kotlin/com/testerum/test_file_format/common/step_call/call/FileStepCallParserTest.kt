package com.testerum.test_file_format.common.step_call.call

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileStepCallParserTest {

    private val parser = ParserExecuter(
        FileStepCallParserFactory.stepCall()
    )

    @Test
    fun `should throw exception for missing step keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("Given an empty database")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "Given an empty database\n" +
                    "^--- ERROR at line 1, column 1: [step] expected, [G] encountered"
            )
    }

    @Test
    fun `should throw exception for missing phase`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("step: an empty database")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "step: an empty database\n" +
                    "      ^--- ERROR at line 1, column 7: [Given, When or Then] expected, [a] encountered"
            )
    }

    @Test
    fun `should parse simple call`() {
        assertThat(parser.parse("step: Given an empty database"))
            .isEqualTo(
                FileStepCall(
                    phase = FileStepPhase.GIVEN,
                    parts = listOf(
                        FileTextStepCallPart("an empty database")
                    )
                )
            )
    }

    @Test
    fun `should parse call with arguments, no expressions`() {
        assertThat(parser.parse("step: When I type <<jdoe@example.com>> in the <<.login>> input"))
            .isEqualTo(
                FileStepCall(
                    phase = FileStepPhase.WHEN,
                    parts = listOf(
                        FileTextStepCallPart("I type "),
                        FileArgStepCallPart("jdoe@example.com"),
                        FileTextStepCallPart(" in the "),
                        FileArgStepCallPart(".login"),
                        FileTextStepCallPart(" input")
                    )
                )
            )
    }

    @Test
    fun `should parse call with arguments, with expressions`() {
        assertThat(
            parser.parse("step: When I type <<{{username}}@{{host}}>> in the <<.{{cssClassName}}>> input")
        ).isEqualTo(
            FileStepCall(
                phase = FileStepPhase.WHEN,
                parts = listOf(
                    FileTextStepCallPart("I type "),
                    FileArgStepCallPart("{{username}}@{{host}}"),
                    FileTextStepCallPart(" in the "),
                    FileArgStepCallPart(".{{cssClassName}}"),
                    FileTextStepCallPart(" input")
                )
            )
        )
    }

    @Test
    fun `should parse call with escaped text parts`() {
        assertThat(parser.parse("""step: Given the \<< stuff"""))
            .isEqualTo(
                FileStepCall(
                    phase = FileStepPhase.GIVEN,
                    parts = listOf(
                        FileTextStepCallPart("the << stuff")
                    )
                )
            )
    }

    @Test
    fun `should parse call with escaped arg ending`() {
        assertThat(parser.parse("""step: Given <<left \>> right>>"""))
            .isEqualTo(
                FileStepCall(
                    phase = FileStepPhase.GIVEN,
                    parts = listOf(
                        FileArgStepCallPart("""left >> right""")
                    )
                )
            )
    }

}
