package com.testerum.test_file_format.feature

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileFeatureSerializerTest {

    private val testRunner = SerializerTestRunner(
        FileFeatureSerializer,
        FileFeatureParserFactory.feature()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
            original = FileFeature(
                description = """ |This feature will do wonderful things.
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
                beforeEachHooks = listOf(
                    FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                            FileTextStepCallPart("I login")
                        ),
                        vars = emptyList()
                    ),
                    FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                            FileTextStepCallPart("I delete "),
                            FileArgStepCallPart("{{stuffToBeDeleted}}")
                        ),
                        vars = listOf(
                            FileStepVar("stuffToBeDeleted", "THE STUFF")
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
                ),
                afterAllHooks = listOf(
                    FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                            FileTextStepCallPart("I shutdown the database "),
                            FileArgStepCallPart("file:{{rdbmsConnectionResource}}")
                        ),
                        vars = listOf(
                            FileStepVar("rdbmsConnectionResource", "Logging Db.rdbms.connection.json")
                        )
                    ),
                    FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                            FileTextStepCallPart("I logout")
                        ),
                        vars = emptyList()
                    )
                )
            ),
            indentLevel = 0,
            expected = """|description = <<
                          |    This feature will do wonderful things.
                          |    It will surpass your wildest expectations.
                          |>>
                          |
                          |tags = <<one, two, three>>
                          |
                          |before-all-hook: Given a clean database
                          |before-all-hook: When writing <<file:Insert Owner.sql>> in <<file:{{rdbmsConnectionResource}}>> database
                          |    var rdbmsConnectionResource = <<Petclinic MySql.rdbms.connection.json>>
                          |
                          |before-each-hook: When I login
                          |before-each-hook: When I delete <<{{stuffToBeDeleted}}>>
                          |    var stuffToBeDeleted = <<THE STUFF>>
                          |
                          |after-each-hook: When I take a screenshot
                          |after-each-hook: When I write debug information to <<file:{{rdbmsConnectionResource}}>>
                          |    var rdbmsConnectionResource = <<Logging Db.rdbms.connection.json>>
                          |
                          |after-all-hook: When I shutdown the database <<file:{{rdbmsConnectionResource}}>>
                          |    var rdbmsConnectionResource = <<Logging Db.rdbms.connection.json>>
                          |after-all-hook: When I logout
                          |""".trimMargin()
        )
    }

    @Test
    fun `indentation test`() {
        assertEquals(
            """|        description = <<
               |            This feature will do wonderful things.
               |            It will surpass your wildest expectations.
               |        >>
               |
               |        tags = <<one, two, three>>
               |
               |        before-all-hook: Given a clean database
               |        before-all-hook: When writing <<file:Insert Owner.sql>> in <<file:{{rdbmsConnectionResource}}>> database
               |            var rdbmsConnectionResource = <<Petclinic MySql.rdbms.connection.json>>
               |
               |        before-each-hook: When I login
               |        before-each-hook: When I delete <<{{stuffToBeDeleted}}>>
               |            var stuffToBeDeleted = <<THE STUFF>>
               |
               |        after-each-hook: When I take a screenshot
               |        after-each-hook: When I write debug information to <<file:{{rdbmsConnectionResource}}>>
               |            var rdbmsConnectionResource = <<Logging Db.rdbms.connection.json>>
               |
               |        after-all-hook: When I shutdown the database <<file:{{rdbmsConnectionResource}}>>
               |            var rdbmsConnectionResource = <<Logging Db.rdbms.connection.json>>
               |        after-all-hook: When I logout
               |""".trimMargin(),
            FileFeatureSerializer.serializeToString(
                FileFeature(
                    description = """ |This feature will do wonderful things.
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
                    beforeEachHooks = listOf(
                        FileStepCall(
                            phase = FileStepPhase.WHEN,
                            parts = listOf(
                                FileTextStepCallPart("I login")
                            ),
                            vars = emptyList()
                        ),
                        FileStepCall(
                            phase = FileStepPhase.WHEN,
                            parts = listOf(
                                FileTextStepCallPart("I delete "),
                                FileArgStepCallPart("{{stuffToBeDeleted}}")
                            ),
                            vars = listOf(
                                FileStepVar("stuffToBeDeleted", "THE STUFF")
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
                    ),
                    afterAllHooks = listOf(
                        FileStepCall(
                            phase = FileStepPhase.WHEN,
                            parts = listOf(
                                FileTextStepCallPart("I shutdown the database "),
                                FileArgStepCallPart("file:{{rdbmsConnectionResource}}")
                            ),
                            vars = listOf(
                                FileStepVar("rdbmsConnectionResource", "Logging Db.rdbms.connection.json")
                            )
                        ),
                        FileStepCall(
                            phase = FileStepPhase.WHEN,
                            parts = listOf(
                                FileTextStepCallPart("I logout")
                            ),
                            vars = emptyList()
                        )
                    )
                ),
                2
            )
        )
    }

}
