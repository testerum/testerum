package com.testerum.test_file_format.common.step_call.call

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
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

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "Given an empty database\n" +
                        "^--- ERROR at line 1, column 1: [step:] expected, [G] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing phase`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("step: an empty database")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                        "step: an empty database\n" +
                        "      ^--- ERROR at line 1, column 7: [Given, When or Then] expected, [a] encountered"
                )
        )
    }

    @Test
    fun `should parse simple call`() {
        assertThat(
                parser.parse("step: Given an empty database"),
                equalTo(
                        FileStepCall(
                                phase = FileStepPhase.GIVEN,
                                parts = listOf(
                                        FileTextStepCallPart("an empty database")
                                )
                        )
                )
        )
    }

    @Test
    fun `should parse call with arguments, no expressions`() {
        assertThat(
                parser.parse("step: When I type <<jdoe@example.com>> in the <<.login>> input"),
                equalTo(
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
        )
    }

    @Test
    fun `should parse call with arguments, with expressions`() {
        assertThat(
                parser.parse("step: When I type <<{{username}}@{{host}}>> in the <<.{{cssClassName}}>> input"),
                equalTo(
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
        )
    }

}