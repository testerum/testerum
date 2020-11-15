package com.testerum.test_file_format.feature

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FileFeatureParserTest {

    private val parser: ParserExecuter<FileFeature> = ParserExecuter(
            FileFeatureParserFactory.feature()
    )

    @Test
    fun `should be able to parse only description`() {
        assertThat(
                parser.parse(
                        """ |    description = <<
                            |       This feature will do wonderful things.
                            |       It will surpass your wildest expectations.
                            |    >>
                        """.trimMargin()
                ),
                equalTo(
                        FileFeature(
                                description =  """ |This feature will do wonderful things.
                                                   |It will surpass your wildest expectations.""".trimMargin()
                        )
                )
        )
    }

    @Test
    fun `test everything`() {
        assertThat(
                parser.parse(
                        """ |    description = <<
                            |       This feature will do wonderful things.
                            |       It will surpass your wildest expectations.
                            |    >>
                            |
                            |    tags = <<one, two, three>>
                            |
                            |    before-hook: Given a clean database
                            |
                            |    before-hook: When writing <<file:Insert Owner.sql>> in <<file:{{rdbmsConnectionResource}}>> database
                            |       var rdbmsConnectionResource = <<Petclinic MySql.rdbms.connection.json>>
                            |
                            |    after-hook: When I take a screenshot
                            |
                            |    after-hook: When I write debug information to <<file:{{rdbmsConnectionResource}}>>
                            |       var rdbmsConnectionResource = <<
                            |           Logging Db.rdbms.connection.json
                            |       >>
                            |
                        """.trimMargin()
                ),
                equalTo(
                        FileFeature(
                                description =  """ |This feature will do wonderful things.
                                                   |It will surpass your wildest expectations.""".trimMargin(),
                                tags = listOf("one", "two", "three"),
                                beforeAllHooks = listOf(
                                        FileStepCall(
                                                phase = FileStepPhase.GIVEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("a clean database")
                                                ),
                                                vars = emptyList()
                                        ),
                                        FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("writing "),
                                                        FileArgStepCallPart("file:Insert Owner.sql"),
                                                        FileTextStepCallPart(" in "),
                                                        FileArgStepCallPart("file:{{rdbmsConnectionResource}}"),
                                                        FileTextStepCallPart(" database")
                                                ),
                                                vars = listOf(
                                                        FileStepVar("rdbmsConnectionResource", "Petclinic MySql.rdbms.connection.json")
                                                )
                                        )
                                ),
                                afterEachHooks = listOf(
                                        FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I take a screenshot")
                                                ),
                                                vars = emptyList()
                                        ),
                                        FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I write debug information to "),
                                                        FileArgStepCallPart("file:{{rdbmsConnectionResource}}")
                                                ),
                                                vars = listOf(
                                                        FileStepVar("rdbmsConnectionResource", "Logging Db.rdbms.connection.json")
                                                )
                                        )
                                )
                        )
                )
        )
    }

}
