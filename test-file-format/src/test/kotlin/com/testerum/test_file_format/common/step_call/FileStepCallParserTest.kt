package com.testerum.test_file_format.common.step_call

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FileStepCallParserTest {

    private val parser = ParserExecuter(
            FileStepCallParserFactory.stepCall()
    )

    @Test
    fun `should parse steps without expressionArgParts`() {
        assertThat(
                parser.parse("step: Given an empty database"),
                equalTo(
                        FileStepCall(
                                phase = FileStepPhase.GIVEN,
                                parts = listOf(
                                        FileTextStepCallPart("an empty database")
                                ),
                                vars = emptyList()
                        )
                )
        )
    }

    @Test
    fun `should parse steps with one expressionArgPart`() {
        assertThat(
                parser.parse(
                        """ |step: When I type <<{{text}}>> into the <<.{{cssClassName}}>> input
                            |    var text = <<
                            |        First line
                            |        Second line
                            |        Third line
                            |    >>
                            |    var cssClassName = <<search>>
                        """.trimMargin()

                ),
                equalTo(
                        FileStepCall(
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
                        )
                )
        )
    }

}