package com.testerum.test_file_format.testdef

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.test_util.SerializerTestRunner
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileTestDefSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileTestDefSerializer,
            FileTestDefParserFactory.testDef()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileTestDef(
                        name = "The name of the test",
                        properties = FileTestDefProperties(isManual = false, isDisabled = false),
                        description = """ |This is a wonderful test.
                                          |It tests many cools things.
                                          |Just read it and you'll see what I mean.
                                          |Escaped >> multiline end.""".trimMargin(),
                        tags = listOf("one", "two", "three", "four"),
                        steps = listOf(
                                FileStepCall(
                                        phase = FileStepPhase.GIVEN,
                                        parts = listOf(
                                                FileTextStepCallPart("some initial state")
                                        )
                                ),
                                FileStepCall(
                                        phase = FileStepPhase.WHEN,
                                        parts = listOf(
                                                FileTextStepCallPart("I do some action")
                                        )
                                ),
                                FileStepCall(
                                        phase = FileStepPhase.THEN,
                                        parts = listOf(
                                                FileTextStepCallPart("something should happen")
                                        )
                                )
                        )
                ),
                indentLevel = 0,
                expected = """|test-def: The name of the test
                              |
                              |    description = <<
                              |        This is a wonderful test.
                              |        It tests many cools things.
                              |        Just read it and you'll see what I mean.
                              |        Escaped \>\> multiline end.
                              |    >>
                              |
                              |    tags = <<one, two, three, four>>
                              |
                              |    step: Given some initial state
                              |    step: When I do some action
                              |    step: Then something should happen
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
                   |            step: Given some initial state
                   |            step: When I do some action
                   |            step: Then something should happen
                   |""".trimMargin(),
                FileTestDefSerializer.serializeToString(
                        FileTestDef(
                                name = "The name of the test",
                                properties = FileTestDefProperties(isManual = false, isDisabled = false),
                                description = """ |This is a wonderful test.
                                          |It tests many cools things.
                                          |Just read it and you'll see what I mean.""".trimMargin(),
                                tags = listOf("one", "two", "three", "four"),
                                steps = listOf(
                                        FileStepCall(
                                                phase = FileStepPhase.GIVEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("some initial state")
                                                )
                                        ),
                                        FileStepCall(
                                                phase = FileStepPhase.WHEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("I do some action")
                                                )
                                        ),
                                        FileStepCall(
                                                phase = FileStepPhase.THEN,
                                                parts = listOf(
                                                        FileTextStepCallPart("something should happen")
                                                )
                                        )
                                )
                        ),
                        2
                )
        )
    }

    @Test
    fun `manual, disabled test`() {
        assertEquals(
                """|test-def: Some test
                   |
                   |    test-properties = <<manual, disabled>>
                   |""".trimMargin(),
                FileTestDefSerializer.serializeToString(
                        FileTestDef(
                                name = "Some test",
                                properties = FileTestDefProperties(isManual = true, isDisabled = true)
                        )
                )
        )
    }

}
