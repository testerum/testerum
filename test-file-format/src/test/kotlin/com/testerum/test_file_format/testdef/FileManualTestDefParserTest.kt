package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus
import com.testerum.test_file_format.manual_test.FileManualTestDef
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FileManualTestDefParserTest {

    private val parser: ParserExecuter<FileManualTestDef> = ParserExecuter(
            FileTestDefParserFactory.manualTestDef()
    )

    @Test
    fun test() {
        assertThat(
                parser.parse(
                        """ |test-def: Successful login
                            |
                            |    description = <<
                            |       A composed step that allows us to bypass the login screen.
                            |       Will be useful from many tests.
                            |    >>
                            |
                            |    tags = <<one, two, three>>
                            |
                            |    step [PASSED]: Given I go to page <<https://{{host}}:{{port}}/login>>
                            |    step [PASSED]: When I type <<{{username}}>> into the <<.username>> input
                            |    step [PASSED]: When I type <<{{password}}>> into the <<.password>> input
                            |
                            |    step [FAILED]: When I type <<{{text}}>> into the <<.{{cssClassName}}>> input
                            |       var text = <<
                            |           I hereby pledge not to do any evil.
                            |           Cross my heart!
                            |       >>
                            |       var cssClassName = <<acknowledgementTextArea>>
                            |
                            |    step [NOT_EXECUTED]: When I set the <<.rememberMe>> checkbox to <<checked>>
                            |    step [NOT_EXECUTED]: When I click the <<.login>> button
                            |
                            |    status = IN_PROGRESS
                            |
                            |    comments = <<
                            |        First comment line.
                            |        Second comment line.
                            |        Another comment line.
                            |    >>
                            |""".trimMargin()
                ),
                equalTo(
                        FileManualTestDef(
                                name = "Successful login",
                                description = """ |A composed step that allows us to bypass the login screen.
                                                   |Will be useful from many tests.""".trimMargin(),
                                tags = listOf("one", "two", "three"),
                                stepCalls = listOf(
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.GIVEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("I go to page "),
                                                                FileArgStepCallPart("https://{{host}}:{{port}}/login")
                                                        ),
                                                        vars = emptyList()
                                                ),
                                                status = FileManualStepCallStatus.PASSED
                                        ),
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.WHEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("I type "),
                                                                FileArgStepCallPart("{{username}}"),
                                                                FileTextStepCallPart(" into the "),
                                                                FileArgStepCallPart(".username"),
                                                                FileTextStepCallPart(" input")
                                                        ),
                                                        vars = emptyList()
                                                ),
                                                status = FileManualStepCallStatus.PASSED
                                        ),
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.WHEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("I type "),
                                                                FileArgStepCallPart("{{password}}"),
                                                                FileTextStepCallPart(" into the "),
                                                                FileArgStepCallPart(".password"),
                                                                FileTextStepCallPart(" input")
                                                        ),
                                                        vars = emptyList()
                                                ),
                                                status = FileManualStepCallStatus.PASSED
                                        ),
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
                                                                        value = """ |I hereby pledge not to do any evil.
                                                                            |Cross my heart!""".trimMargin()
                                                                ),
                                                                FileStepVar(name = "cssClassName", value = "acknowledgementTextArea")
                                                        )
                                                ),
                                                status = FileManualStepCallStatus.FAILED
                                        ),
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.WHEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("I set the "),
                                                                FileArgStepCallPart(".rememberMe"),
                                                                FileTextStepCallPart(" checkbox to "),
                                                                FileArgStepCallPart("checked")
                                                        ),
                                                        vars = emptyList()
                                                ),
                                                status = FileManualStepCallStatus.NOT_EXECUTED
                                        ),
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.WHEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("I click the "),
                                                                FileArgStepCallPart(".login"),
                                                                FileTextStepCallPart(" button")
                                                        ),
                                                        vars = emptyList()
                                                ),
                                                status = FileManualStepCallStatus.NOT_EXECUTED
                                        )
                                ),
                                status = FileManualTestStatus.IN_PROGRESS,
                                comments = """ |First comment line.
                                               |Second comment line.
                                               |Another comment line.""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `should allow test with status, but without steps`() {
        assertThat(
                parser.parse(
                        """ |test-def: Empty test
                            |
                            |    status = NOT_EXECUTED
                            |
                        """.trimMargin()
                ),
                equalTo(
                        FileManualTestDef(
                                name = "Empty test",
                                description = null,
                                tags = emptyList(),
                                stepCalls = emptyList(),
                                status = FileManualTestStatus.NOT_EXECUTED,
                                comments = null
                        )
                )
        )
    }

}