package com.testerum.test_file_format.common.step_call.phase

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Test

class FileStepPhaseSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileStepPhaseSerializer,
            FileStepPhaseParserFactory.stepPhase()
    )

    @Test
    fun given() {
        testRunner.execute(
                original = FileStepPhase.GIVEN,
                indentLevel = 0,
                expected = "Given"
        )
    }

    @Test
    fun `when`() {
        testRunner.execute(
                original = FileStepPhase.WHEN,
                indentLevel = 0,
                expected = "When"
        )
    }

    @Test
    fun then() {
        testRunner.execute(
                original = FileStepPhase.THEN,
                indentLevel = 0,
                expected = "Then"
        )
    }

}