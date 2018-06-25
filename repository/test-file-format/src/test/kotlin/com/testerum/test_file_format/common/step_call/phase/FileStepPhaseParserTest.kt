package com.testerum.test_file_format.common.step_call.phase

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileStepPhaseParserTest {

    private val parser = ParserExecuter(
            FileStepPhaseParserFactory.stepPhase()
    )

    @Test
    fun `should throw exception for invalid phase`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("stuff")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                                "stuff\n" +
                                "^--- ERROR at line 1, column 1: [Given, When or Then] expected, [s] encountered"
                )
        )
    }

    @Test
    fun `should parse given`() {
        assertThat(
                parser.parse("Given"),
                equalTo(FileStepPhase.GIVEN)
        )
    }

    @Test
    fun `should parse when`() {
        assertThat(
                parser.parse("When"),
                equalTo(FileStepPhase.WHEN)
        )
    }

    @Test
    fun `should parse then`() {
        assertThat(
                parser.parse("Then"),
                equalTo(FileStepPhase.THEN)
        )
    }

}