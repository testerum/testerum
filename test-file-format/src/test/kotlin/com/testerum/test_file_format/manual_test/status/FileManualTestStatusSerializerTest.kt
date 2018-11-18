package com.testerum.test_file_format.manual_test.status

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class FileManualTestStatusSerializerTest {

    companion object {
        private val testRunner = SerializerTestRunner(
                FileManualTestStatusSerializer,
                FileManualTestStatusParserFactory.manualTestStatus()
        )

        @JvmStatic
        @Suppress("unused")
        fun provideTestArguments(): Stream<Arguments> {
            return FileManualTestStatus.values().map {
                Arguments.of(it)
            }.stream()
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun test(statusToTest: FileManualTestStatus) {
        testRunner.execute(
                original = statusToTest,
                indentLevel = 0,
                expected = "status = ${statusToTest.name}\n"
        )
    }

}