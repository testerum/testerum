package com.testerum.test_file_format.manual_step_call

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus
import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileStepCallSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileManualStepCallSerializer,
            FileStepCallParserFactory.manualStepCall()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileManualStepCall(
                        step = FileStepCall(
                                phase = FileStepPhase.WHEN,
                                parts = listOf(
                                        FileTextStepCallPart("I click the element "),
                                        FileArgStepCallPart("elementLocator")
                                ),
                                vars = emptyList()
                        ),
                        status = FileManualStepCallStatus.FAILED
                ),
                indentLevel = 0,
                expected = "step [FAILED]: When I click the element <<elementLocator>>\n"
        )
    }

    @Test
    fun `indentation test and variables`() {
        assertEquals(
                """|        step [NOT_EXECUTED]: When writing <<file:Insert Owner.sql>> in <<file:{{rdbmsConnectionResource}}>> database
                   |            var rdbmsConnectionResource = <<Petclinic MySql.rdbms.connection.json>>
                   |
                """.trimMargin(),
                FileManualStepCallSerializer.serializeToString(
                        FileManualStepCall(
                                step = FileStepCall(
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
                                status = FileManualStepCallStatus.NOT_EXECUTED
                        ),
                        2
                )
        )
    }

}