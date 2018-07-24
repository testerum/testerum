package com.testerum.test_file_format.testdef.properties

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileTestDefPropertiesSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileTestDefPropertiesSerializer,
            FileTestDefPropertiesParserFactory.testProperties()
    )

    @Test
    fun `not manual, not disabled`() {
        testRunner.execute(
                original = FileTestDefProperties(isManual = false, isDisabled = false),
                indentLevel = 0,
                expected = "test-properties = <<>>\n"
        )
    }

    @Test
    fun `not manual, disabled`() {
        testRunner.execute(
                original = FileTestDefProperties(isManual = false, isDisabled = true),
                indentLevel = 0,
                expected = "test-properties = <<disabled>>\n"
        )
    }

    @Test
    fun `manual, not disabled`() {
        testRunner.execute(
                original = FileTestDefProperties(isManual = true, isDisabled = false),
                indentLevel = 0,
                expected = "test-properties = <<manual>>\n"
        )
    }

    @Test
    fun `manual, disabled`() {
        testRunner.execute(
                original = FileTestDefProperties(isManual = true, isDisabled = true),
                indentLevel = 0,
                expected = "test-properties = <<manual, disabled>>\n"
        )
    }

    @Test
    fun `indentation test`() {
        assertEquals(
                """|        test-properties = <<manual, disabled>>
                   |
                """.trimMargin(),
                FileTestDefPropertiesSerializer.serializeToString(
                        FileTestDefProperties(isManual = true, isDisabled = true),
                        2
                )
        )
    }

}