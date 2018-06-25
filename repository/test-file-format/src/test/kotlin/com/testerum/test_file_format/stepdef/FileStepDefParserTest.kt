package com.testerum.test_file_format.stepdef

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignature
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FileStepDefParserTest {

    private val parser = ParserExecuter(FileStepDefParserFactory.stepDef())

    @Test
    fun test() {
        assertThat(
                parser.parse(
                        """ |step-def: Given I login as <<username: string>>/<<password: string>> to <<host: string>>/<<port: int>>
                            |
                            |    description = <<
                            |       A composed step that allows us to bypass the login screen.
                            |       Will be useful from many tests.
                            |    >>
                            |
                            |    step: Given I go to page <<https://{{host}}:{{port}}/login>>
                            |    step: When I type <<{{username}}>> into the <<.username>> input
                            |    step: When I type <<{{password}}>> into the <<.password>> input
                            |
                            |    step: When I type <<{{text}}>> into the <<.{{cssClassName}}>> input
                            |       var text = <<
                            |           I hereby pledge not to do any evil.
                            |           Cross my heart!
                            |       >>
                            |       var cssClassName = <<acknowledgementTextArea>>
                            |
                            |    step: When I set the <<.rememberMe>> checkbox to <<checked>>
                            |    step: When I click the <<.login>> button
                        """.trimMargin()
                ),
                equalTo(
                        FileStepDef(
                                signature = FileStepDefSignature(
                                        phase = FileStepPhase.GIVEN,
                                        parts = listOf(
                                                FileTextStepDefSignaturePart("I login as "),
                                                FileParamStepDefSignaturePart(name = "username", type = "string"),
                                                FileTextStepDefSignaturePart("/"),
                                                FileParamStepDefSignaturePart(name = "password", type = "string"),
                                                FileTextStepDefSignaturePart(" to "),
                                                FileParamStepDefSignaturePart(name = "host", type = "string"),
                                                FileTextStepDefSignaturePart("/"),
                                                FileParamStepDefSignaturePart(name = "port", type = "int")
                                        )
                                ),
                                description = """ |A composed step that allows us to bypass the login screen.
                                                  |Will be useful from many tests.""".trimMargin(),
                                steps = listOf(
                                        FileStepCall(
                                                phase = FileStepPhase.GIVEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I go to page "),
                                                        FileArgStepCallPart("https://{{host}}:{{port}}/login")
                                                ),
                                                vars = emptyList()
                                        ),
                                        FileStepCall(
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
                                        FileStepCall(
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
                                                                value = """ |I hereby pledge not to do any evil.
                                                                            |Cross my heart!""".trimMargin()
                                                        ),
                                                        FileStepVar(name = "cssClassName", value = "acknowledgementTextArea")
                                                )
                                        ),
                                        FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I set the "),
                                                        FileArgStepCallPart(".rememberMe"),
                                                        FileTextStepCallPart(" checkbox to "),
                                                        FileArgStepCallPart("checked")
                                                ),
                                                vars = emptyList()
                                        ),
                                        FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I click the "),
                                                        FileArgStepCallPart(".login"),
                                                        FileTextStepCallPart(" button")
                                                ),
                                                vars = emptyList()
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun `can parse parameter types as fully qualified class names`() {
        assertThat(
                parser.parse(
                        """ |step-def: Given a step and a <<parameter: java.lang.String>>
                        """.trimMargin()
                ),
                equalTo(
                        FileStepDef(
                                signature = FileStepDefSignature(
                                        phase = FileStepPhase.GIVEN,
                                        parts = listOf(
                                                FileTextStepDefSignaturePart("a step and a "),
                                                FileParamStepDefSignaturePart(name = "parameter", type = "java.lang.String")
                                        )
                                )
                        )
                )
        )
    }
}