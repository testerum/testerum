package com.testerum.test_file_format.common.step_call.call.step_call_part

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileStepCallPartParserFactory
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileStepCallPartParserTest {

    // todo: re-purpose this test to be for FileArgPartParserFactory

    private val parser = ParserExecuter(
        FileStepCallPartParserFactory.stepCallPart()
    )

    @Test
    fun `should parse text part`() {
        assertThat(parser.parse("some text"))
            .isEqualTo(FileTextStepCallPart("some text") as FileStepCallPart)
    }

    @Test
    fun `should throw exception for invalid arg - missing closing separator`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("<<arg value")
        }

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "<<arg value\n" +
                    "           ^--- ERROR at line 1, column 12: [\\>, (not newline or >>) or >>] expected, [EOF] encountered"
            )
    }

    @Test
    fun `should parse empty arg`() {
        assertThat(parser.parse("<<>>"))
            .isEqualTo(FileArgStepCallPart("") as FileStepCallPart)
    }

    @Test
    fun `should parse arg with one text part`() {
        assertThat(parser.parse("<<some text>>"))
            .isEqualTo(
                FileArgStepCallPart("some text") as FileStepCallPart
            )
    }

    @Test
    fun `should parse arg with one expression part alone`() {
        assertThat(parser.parse("<<{{variable}}>>"))
            .isEqualTo(
                FileArgStepCallPart("{{variable}}") as FileStepCallPart
            )
    }

    @Test
    fun `should parse arg with one expression part at the beginning`() {
        assertThat(parser.parse("<<{{variable}} after>>"))
            .isEqualTo(
                FileArgStepCallPart("{{variable}} after") as FileStepCallPart
            )
    }

    @Test
    fun `should parse arg with one expression part in the middle`() {
        assertThat(parser.parse("<<before {{variable}} after>>"))
            .isEqualTo(
                FileArgStepCallPart("before {{variable}} after") as FileStepCallPart
            )
    }

    @Test
    fun `should parse arg with one expression part at the end`() {
        assertThat(parser.parse("<<before {{variable}}>>"))
            .isEqualTo(
                FileArgStepCallPart("before {{variable}}") as FileStepCallPart
            )
    }

    @Test
    fun `should parse arg with multiple parts of all types`() {
        assertThat(parser.parse("<<beginning {{var1}} middle one {{var2}} middle two {{var3}} end>>"))
            .isEqualTo(
                FileArgStepCallPart("beginning {{var1}} middle one {{var2}} middle two {{var3}} end") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse backslash, no expressionArgParts`() {
        assertThat(parser.parse("""<<2 \ 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""2 \ 1""") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse one greaterThan, no escape needed, no expressionArgParts`() {
        assertThat(parser.parse("""<<2 > 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""2 > 1""") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse escaped backslash followed by double greaterThan, no expressionArgParts`() {
        assertThat(parser.parse("""<<2 \\>> 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""2 \>> 1""") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse multiple escapes, no expressionArgParts`() {
        assertThat(parser.parse("""<<5000 \>> 200 \>> 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""5000 >> 200 >> 1""") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse backslash, with expressionArgParts`() {
        assertThat(parser.parse("""<<2 \ {{var}} \ 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""2 \ {{var}} \ 1""") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse one greaterThan, no escape needed, with expressionArgParts`() {
        assertThat(parser.parse("""<<2 > {{var}} > 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""2 > {{var}} > 1""") as FileStepCallPart
            )
    }

    @Test
    fun `escaping - should parse escaped backslash followed by double greaterThan, with expressionArgParts`() {
        assertThat(parser.parse("""<<2 \\>> {{var}} \\>> 1>>"""))
            .isEqualTo(
                FileArgStepCallPart("""2 \>> {{var}} \>> 1""") as FileStepCallPart
            )
    }

}
