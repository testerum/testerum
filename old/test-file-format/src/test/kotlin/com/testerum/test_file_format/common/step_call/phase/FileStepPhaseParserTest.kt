package com.testerum.test_file_format.common.step_call.phase

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.assertj.core.api.Assertions.assertThat
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

        assertThat(exception.message)
            .isEqualTo(
                "failed to parse:\n" +
                    "stuff\n" +
                    "^--- ERROR at line 1, column 1: [Given, When or Then] expected, [s] encountered"
            )
    }

    @Test
    fun `should parse given`() {
        assertThat(parser.parse("Given"))
            .isEqualTo(FileStepPhase.GIVEN)
    }

    @Test
    fun `should parse when`() {
        assertThat(parser.parse("When"))
            .isEqualTo(FileStepPhase.WHEN)
    }

    @Test
    fun `should parse then`() {
        assertThat(parser.parse("Then"))
            .isEqualTo(FileStepPhase.THEN)
    }

}
