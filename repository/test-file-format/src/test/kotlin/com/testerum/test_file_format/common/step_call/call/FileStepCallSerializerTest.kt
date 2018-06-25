package com.testerum.test_file_format.common.step_call.call

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory
import com.testerum.test_file_format.common.step_call.FileStepCallSerializer
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Test

class FileStepCallSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileStepCallSerializer,
            FileStepCallParserFactory.stepCall()
    )

    @Test
    fun `simple call`() {
        testRunner.execute(
                original = FileStepCall(
                        phase = FileStepPhase.GIVEN,
                        parts = listOf(
                                FileTextStepCallPart("an empty database")
                        )
                ),
                indentLevel = 0,
                expected = "step: Given an empty database\n"
        )

    }

    @Test
    fun `with arguments, no expressions`() {
        testRunner.execute(
                original = FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                                FileTextStepCallPart("I type "),
                                FileArgStepCallPart("jdoe@example.com"),
                                FileTextStepCallPart(" in the "),
                                FileArgStepCallPart(".login"),
                                FileTextStepCallPart(" input")
                        )
                ),
                indentLevel = 0,
                expected = "step: When I type <<jdoe@example.com>> in the <<.login>> input\n"
        )
    }

    @Test
    fun `with arguments, with expressions`() {
        testRunner.execute(
                original = FileStepCall(
                        phase = FileStepPhase.WHEN,
                        parts = listOf(
                                FileTextStepCallPart("I type "),
                                FileArgStepCallPart("{{username}}@{{host}}"),
                                FileTextStepCallPart(" in the "),
                                FileArgStepCallPart(".{{cssClassName}}"),
                                FileTextStepCallPart(" input")
                        )
                ),
                indentLevel = 0,
                expected = "step: When I type <<{{username}}@{{host}}>> in the <<.{{cssClassName}}>> input\n"
        )
    }


}