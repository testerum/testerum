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
            FileManualStepCallPropertiesParserFactory.manualStepProperties()
        )

        @JvmStatic
        @Suppress("unused")
        fun provideTestArguments(): Stream<Arguments> {
            val arguments = mutableListOf<Arguments>()

            for (status in FileManualStepCallStatus.values()) {
                arguments += Arguments.of(
                    FileManualStepCallProperties(status, enabled = false)
                )
                arguments += Arguments.of(
                    FileManualStepCallProperties(status, enabled = true)
                )
            }

            return arguments.stream()
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun test(propertyToTest: FileManualStepCallProperties) {
        val expected = if (propertyToTest.enabled) {
            "[${propertyToTest.status.name}]"
        } else {
            "[${propertyToTest.status.name}, disabled]"
        }

        testRunner.execute(
            original = propertyToTest,
            indentLevel = 0,
            expected = expected
        )
    }

}
