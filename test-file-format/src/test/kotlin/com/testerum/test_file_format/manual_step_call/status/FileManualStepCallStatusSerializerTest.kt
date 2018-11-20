package com.testerum.test_file_format.manual_step_call.status

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class FileManualStepCallStatusSerializerTest {

    companion object {
        private val testRunner = SerializerTestRunner(
                FileManualStepCallStatusSerializer,
                FileManualStepCallStatusParserFactory.manualStepStatus()
        )

        @JvmStatic
        @Suppress("unused")
        fun provideTestArguments(): Stream<Arguments> {
            return FileManualStepCallStatus.values().map {
                Arguments.of(it)
            }.stream()
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun test(statusToTest: FileManualStepCallStatus) {
        testRunner.execute(
                original = statusToTest,
                indentLevel = 0,
                expected = "[${statusToTest.name}]"
        )
    }

}