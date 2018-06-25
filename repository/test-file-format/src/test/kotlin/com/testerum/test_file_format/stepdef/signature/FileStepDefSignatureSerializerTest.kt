package com.testerum.test_file_format.stepdef.signature

import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileStepDefSignatureSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileStepDefSignatureSerializer,
            FileStepDefSignatureParserFactory.stepDefSignature()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileStepDefSignature(
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
                indentLevel = 0,
                expected = "step-def: Given I login as <<username: string>>/<<password: string>> to <<host: string>>/<<port: int>>"
        )
    }

    @Test
    fun `indentation test`() {
        assertEquals(
                "        step-def: Given some step",
                FileStepDefSignatureSerializer.serializeToString(
                        FileStepDefSignature(
                                phase = FileStepPhase.GIVEN,
                                parts = listOf(
                                        FileTextStepDefSignaturePart("some step")
                                )
                        ),
                        2
                )
        )
    }

}