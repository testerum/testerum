package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import com.testerum.test_file_format.testdef.scenarios.FileScenario
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParam
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParamType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FileTestDefParserTest {

    private val parser: ParserExecuter<FileTestDef> = ParserExecuter(
            FileTestDefParserFactory.testDef()
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
                            |    scenario:
                            |
                            |    scenario: A scenario without params
                            |
                            |    scenario [disabled]: A disabled scenario
                            |        param name = <<value>>
                            |
                            |    scenario: A scenario with description and params
                            |        param firstName = <<John>>
                            |        param lastName = <<Doe>>
                            |        param description = <<
                            |            The ultimate description
                            |            for the unknown guy.
                            |        >>
                            |        param-json info = <<{"person": {"address": {"street": "Eroilor"}}}>>
                            |        param-json multilineInfo = <<
                            |            {
                            |                "person": {
                            |                    "address": {
                            |                        "street": "Eroilor"
                            |                    }
                            |                }
                            |            }
                            |        >>
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
                        FileTestDef(
                                name = "Successful login",
                                properties = FileTestDefProperties(isManual = false, isDisabled = false),
                                description =  """ |A composed step that allows us to bypass the login screen.
                                                   |Will be useful from many tests.""".trimMargin(),
                                tags = listOf("one", "two", "three"),
                                scenarios = listOf(
                                        FileScenario(
                                                name = null,
                                                params = emptyList(),
                                                enabled = true
                                        ),
                                        FileScenario(
                                                name = "A scenario without params",
                                                params = emptyList(),
                                                enabled = true
                                        ),
                                        FileScenario(
                                                name = "A disabled scenario",
                                                params = listOf(
                                                        FileScenarioParam(
                                                                name = "name",
                                                                type = FileScenarioParamType.TEXT,
                                                                value = "value"
                                                        )
                                                ),
                                                enabled = false
                                        ),
                                        FileScenario(
                                                name = "A scenario with description and params",
                                                params = listOf(
                                                        FileScenarioParam(
                                                                name = "firstName",
                                                                type = FileScenarioParamType.TEXT,
                                                                value = "John"
                                                        ),
                                                        FileScenarioParam(
                                                                name = "lastName",
                                                                type = FileScenarioParamType.TEXT,
                                                                value = "Doe"
                                                        ),
                                                        FileScenarioParam(
                                                                name = "description",
                                                                type = FileScenarioParamType.TEXT,
                                                                value = """ |The ultimate description
                                                                            |for the unknown guy.""".trimMargin()
                                                        ),
                                                        FileScenarioParam(
                                                                name = "info",
                                                                type = FileScenarioParamType.JSON,
                                                                value = """{"person": {"address": {"street": "Eroilor"}}}"""
                                                        ),
                                                        FileScenarioParam(
                                                                name = "multilineInfo",
                                                                type = FileScenarioParamType.JSON,
                                                                value = """ |{
                                                                            |    "person": {
                                                                            |        "address": {
                                                                            |            "street": "Eroilor"
                                                                            |        }
                                                                            |    }
                                                                            |}""".trimMargin()
                                                        )
                                                ),
                                                enabled = true
                                        )
                                ),
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
    fun `should allow test with only description, but without steps`() {
        assertThat(
                parser.parse(
                        """ |test-def: Empty test
                            |
                            |    description = <<some description>>
                            |
                        """.trimMargin()
                ),
                equalTo(
                        FileTestDef(
                                name = "Empty test",
                                properties = FileTestDefProperties(isManual = false, isDisabled = false),
                                description =  "some description",
                                steps = emptyList()
                        )
                )
        )
    }

    @Test
    fun `manual, disabled test`() {
        assertThat(
                parser.parse(
                        """ |test-def: A test
                            |
                            |    test-properties = <<manual, disabled>>
                        """.trimMargin()
                ),
                equalTo(
                        FileTestDef(
                                name = "A test",
                                properties = FileTestDefProperties(isManual = true, isDisabled = true),
                                steps = emptyList()
                        )
                )
        )
    }

}
