package com.testerum.test_file_format.manual_test

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus
import com.testerum.test_file_format.test_util.SerializerTestRunner
import com.testerum.test_file_format.testdef.FileTestDefParserFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileManualTestDefSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileManualTestDefSerializer,
            FileTestDefParserFactory.manualTestDef()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileManualTestDef(
                        name = "The name of the test",
                        description = """ |This is a wonderful test.
                                          |It tests many cools things.
                                          |Just read it and you'll see what I mean.""".trimMargin(),
                        tags = listOf("one", "two", "three", "four"),
                        stepCalls = listOf(
                                FileManualStepCall(
                                        step = FileStepCall(
                                                phase = FileStepPhase.GIVEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("some initial state")
                                                )
                                        ),
                                        status = FileManualStepCallStatus.PASSED
                                ),
                                FileManualStepCall(
                                        step = FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I do some action")
                                                )
                                        ),
                                        status = FileManualStepCallStatus.FAILED
                                ),
                                FileManualStepCall(
                                        step = FileStepCall(
                                                phase = FileStepPhase.THEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("something should happen")
                                                )
                                        )
                                        ,
                                        status = FileManualStepCallStatus.NOT_EXECUTED
                                )
                        ),
                        status = FileManualTestStatus.BLOCKED,
                        comments = """ |First line of the comments.
                                       |Second line.
                                       |Third.
                                       |4th.""".trimMargin()
                ),
                indentLevel = 0,
                expected = """|test-def: The name of the test
                              |
                              |    description = <<
                              |        This is a wonderful test.
                              |        It tests many cools things.
                              |        Just read it and you'll see what I mean.
                              |    >>
                              |
                              |    tags = <<one, two, three, four>>
                              |
                              |    step [PASSED]: Given some initial state
                              |    step [FAILED]: When I do some action
                              |    step [NOT_EXECUTED]: Then something should happen
                              |
                              |    status = BLOCKED
                              |
                              |    comments = <<
                              |        First line of the comments.
                              |        Second line.
                              |        Third.
                              |        4th.
                              |    >>
                              |""".trimMargin()
        )
    }

    @Test
    fun `indentation test`() {
        assertEquals(
                """|        test-def: The name of the test
                   |
                   |            description = <<
                   |                This is a wonderful test.
                   |                It tests many cools things.
                   |                Just read it and you'll see what I mean.
                   |            >>
                   |
                   |            tags = <<one, two, three, four>>
                   |
                   |            step [PASSED]: Given some initial state
                   |            step [FAILED]: When I do some action
                   |            step [NOT_EXECUTED]: Then something should happen
                   |
                   |            status = BLOCKED
                   |
                   |            comments = <<
                   |                First line of the comments.
                   |                Second line.
                   |                Third.
                   |                4th.
                   |            >>
                   |""".trimMargin(),
                FileManualTestDefSerializer.serializeToString(
                        FileManualTestDef(
                                name = "The name of the test",
                                description = """ |This is a wonderful test.
                                          |It tests many cools things.
                                          |Just read it and you'll see what I mean.""".trimMargin(),
                                tags = listOf("one", "two", "three", "four"),
                                stepCalls = listOf(
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.GIVEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("some initial state")
                                                        )
                                                ),
                                                status = FileManualStepCallStatus.PASSED
                                        ),
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.WHEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("I do some action")
                                                        )
                                                ),
                                                status = FileManualStepCallStatus.FAILED
                                        ),
                                        FileManualStepCall(
                                                step = FileStepCall(
                                                        phase = FileStepPhase.THEN,
                                                        parts = listOf(
                                                                FileTextStepCallPart("something should happen")
                                                        )
                                                )
                                                ,
                                                status = FileManualStepCallStatus.NOT_EXECUTED
                                        )
                                ),
                                status = FileManualTestStatus.BLOCKED,
                                comments = """ |First line of the comments.
                                       |Second line.
                                       |Third.
                                       |4th.""".trimMargin()
                        ),
                        2
                )
        )
    }

    @Test
    fun `should not serialize null description or comments`() {
        assertEquals(
                """|test-def: The name of the test
                   |
                   |    status = NOT_APPLICABLE
                   |""".trimMargin(),
                FileManualTestDefSerializer.serializeToString(
                        FileManualTestDef(
                                name = "The name of the test",
                                description = null,
                                tags = emptyList(),
                                stepCalls = emptyList(),
                                status = FileManualTestStatus.NOT_APPLICABLE,
                                comments = null
                        )
                )
        )
    }

    @Test
    fun `should not serialize empty description or comments`() {
        assertEquals(
                """|test-def: The name of the test
                   |
                   |    status = NOT_APPLICABLE
                   |""".trimMargin(),
                FileManualTestDefSerializer.serializeToString(
                        FileManualTestDef(
                                name = "The name of the test",
                                description = "",
                                tags = emptyList(),
                                stepCalls = emptyList(),
                                status = FileManualTestStatus.NOT_APPLICABLE,
                                comments = ""
                        )
                )
        )
    }

}