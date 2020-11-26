package com.testerum.test_file_format.common.step_call

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileManualStepCallParserTest {

    private val parser = ParserExecuter(
        FileStepCallParserFactory.manualStepCall()
    )

    @Test
    fun `should parse steps without expressionArgParts`() {
        assertThat(parser.parse("step [NOT_EXECUTED]: Given an empty database"))
            .isEqualTo(
                FileManualStepCall(
                    step = FileStepCall(
                        phase = FileStepPhase.GIVEN,
                        parts = listOf(
                            FileTextStepCallPart("an empty database")
                        ),
                        vars = emptyList()
                    ),
                    status = FileManualStepCallStatus.NOT_EXECUTED
                )
            )
    }

    @Test
    fun `should parse disabled step`() {
        assertThat(parser.parse("step [NOT_EXECUTED, disabled]: Given an empty database"))
            .isEqualTo(
                FileManualStepCall(
                    step = FileStepCall(
                        phase = FileStepPhase.GIVEN,
                        parts = listOf(
                            FileTextStepCallPart("an empty database")
                        ),
                        vars = emptyList(),
                        enabled = false
                    ),
                    status = FileManualStepCallStatus.NOT_EXECUTED
                )
            )
    }

    @Test
    fun `should parse steps with one expressionArgPart`() {
        assertThat(
            parser.parse(
                """ |step [PASSED]: When I type <<{{text}}>> into the <<.{{cssClassName}}>> input
                            |    var text = <<
                            |        First line
                            |        Second line
                            |        Third line
                            |    >>
                            |    var cssClassName = <<search>>
                        """.trimMargin()

            )
        ).isEqualTo(
            FileManualStepCall(
                step = FileStepCall(
                    phase = FileStepPhase.WHEN,
                    parts = listOf(
                        FileTextStepCallPart("I type "),
                        FileArgStepCallPart("{{text}}"),
                        FileTextStepCallPart(" into the "),
                        FileArgStepCallPart(".{{cssClassName}}"),
                        FileTextStepCallPart(" input")
                    ),
                    vars = listOf(
                        FileStepVar(
                            name = "text",
                            value = """ |First line
                                                            |Second line
                                                            |Third line""".trimMargin()
                        ),
                        FileStepVar(name = "cssClassName", value = "search")
                    )
                ),
                status = FileManualStepCallStatus.PASSED
            )
        )
    }

}
