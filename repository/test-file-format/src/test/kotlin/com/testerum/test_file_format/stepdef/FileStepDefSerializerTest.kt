package com.testerum.test_file_format.stepdef

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignature
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileStepDefSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileStepDefSerializer,
            FileStepDefParserFactory.stepDef()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileStepDef(
                        signature = FileStepDefSignature(
                                phase = FileStepPhase.WHEN,
                                parts = listOf(
                                        FileTextStepDefSignaturePart("I set the age to "),
                                        FileParamStepDefSignaturePart("age", "TEXT")
                                )
                        ),
                        description = """ |This is a wonderful step definition.
                                          |It's just so reusable, it's unbelievable!""".trimMargin(),
                        tags = listOf("one", "two", "three"),
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
                expected = """|step-def: When I set the age to <<age: TEXT>>
                              |
                              |    description = <<
                              |        This is a wonderful step definition.
                              |        It's just so reusable, it's unbelievable!
                              |    >>
                              |
                              |    tags = <<one, two, three>>
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
                """|        step-def: When I set the age to <<age: TEXT>>
                   |
                   |            description = <<
                   |                This is a wonderful step definition.
                   |                It's just so reusable, it's unbelievable!
                   |            >>
                   |
                   |            tags = <<one, two, three>>
                   |
                   |            step: Given some initial state
                   |            step: When I do some action
                   |            step: Then something should happen
                   |""".trimMargin(),
                FileStepDefSerializer.serializeToString(
                        FileStepDef(
                                signature = FileStepDefSignature(
                                        phase = FileStepPhase.WHEN,
                                        parts = listOf(
                                                FileTextStepDefSignaturePart("I set the age to "),
                                                FileParamStepDefSignaturePart("age", "TEXT")
                                        )
                                ),
                                description = """ |This is a wonderful step definition.
                                          |It's just so reusable, it's unbelievable!""".trimMargin(),
                                tags = listOf("one", "two", "three"),
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

}