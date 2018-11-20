package com.testerum.test_file_format.manual_test.status

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class FileManualTestStatusParserFactoryTest {

    companion object {
        private val parser = ParserExecuter(
                FileManualTestStatusParserFactory.manualTestStatus()
        )

        @JvmStatic
        @Suppress("unused")
        fun provideCorrectTestArguments(): Stream<Arguments> {
            return FileManualTestStatus.values().map {
                Arguments.of(it)
            }.stream()
        }
    }

    @ParameterizedTest
    @MethodSource("provideCorrectTestArguments")
    fun `OK test`(statusToTest: FileManualTestStatus) {
        MatcherAssert.assertThat(
                parser.parse(
                        """ |status = ${statusToTest.name}
                        """.trimMargin()
                ),
                equalTo(statusToTest)
        )
    }

    @Test
    fun `should throw exception for missing status keyword`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("= PASSED")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                                "= PASSED\n" +
                                "^--- ERROR at line 1, column 1: [status] expected, [=] encountered"
                )
        )
    }

    @Test
    fun `should throw exception for missing =`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("status PASSED")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                                "status PASSED\n" +
                                "       ^--- ERROR at line 1, column 8: [=] expected, [P] encountered"
                )
        )
    }

}