package com.testerum.test_file_format.common.tags

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileTagsSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileTagsSerializer,
            FileTagsParserFactory.tags()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = listOf("one", "two", "three"),
                indentLevel = 0,
                expected = "tags = <<one, two, three>>\n"
        )
    }

    @Test
    fun `indentation test`() {
        assertEquals(
                """|        tags = <<one, two, three, four>>
                   |
                """.trimMargin(),
                FileTagsSerializer.serializeToString(
                        listOf("one", "two", "three", "four"),
                        2
                )
        )
    }

}