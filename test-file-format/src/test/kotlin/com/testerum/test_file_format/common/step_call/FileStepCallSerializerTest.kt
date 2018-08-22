package com.testerum.test_file_format.common.step_call

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileStepCallSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileStepCallSerializer,
            FileStepCallParserFactory.stepCall()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                                FileTextStepCallPart("I click the element "),
                                FileArgStepCallPart("elementLocator")
                        ),
                        vars = emptyList()
                ),
                indentLevel = 0,
                expected = "step: When I click the element <<elementLocator>>\n"
        )
    }

    @Test
    fun `indentation test and variables`() {
        assertEquals(
                """|        step: When writing <<file:Insert Owner.sql>> in <<file:{{rdbmsConnectionResource}}>> database
                   |            var rdbmsConnectionResource = <<Petclinic MySql.rdbms.connection.json>>
                   |
                """.trimMargin(),
                FileStepCallSerializer.serializeToString(
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
                        ),
                        2
                )
        )
    }

}