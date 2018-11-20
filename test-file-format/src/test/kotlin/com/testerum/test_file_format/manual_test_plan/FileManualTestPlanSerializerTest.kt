package com.testerum.test_file_format.manual_test_plan

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FileManualTestPlanSerializerTest {


    private val testRunner = SerializerTestRunner(
            FileManualTestPlanSerializer,
            FileManualTestPlanParserFactory.manualTestPlan()
    )

    @Test
    fun `simple test`() {
        testRunner.execute(
                original = FileManualTestPlan(
                        description = """ |This test plan will do wonderful things.
                                          |It will surpass your wildest expectations.""".trimMargin(),
                        createdDateUtc = LocalDateTime.of(1900, 1, 2, 3, 4, 5, 6),
                        finalizedDateUtc = LocalDateTime.of(2000, 10, 20, 3, 40, 50, 60)
                ),
                indentLevel = 0,
                expected = """|description = <<
                              |    This test plan will do wonderful things.
                              |    It will surpass your wildest expectations.
                              |>>
                              |
                              |created-date-utc = 1900-01-02T03:04:05.000000006
                              |
                              |finalized-date-utc = 2000-10-20T03:40:50.000000060
                              |""".trimMargin()
        )
    }

    @Test
    fun `null dates should not be serialized`() {
        testRunner.execute(
                original = FileManualTestPlan(
                        description = "a description",
                        createdDateUtc = null,
                        finalizedDateUtc = null
                ),
                indentLevel = 0,
                expected = """|description = <<a description>>
                              |""".trimMargin()
        )
    }

    @Test
    fun `null description and dates not be serialized`() {
        testRunner.execute(
                original = FileManualTestPlan(
                        description = null,
                        createdDateUtc = null,
                        finalizedDateUtc = null
                ),
                indentLevel = 0,
                expected = ""
        )
    }

    @Test
    fun `indentation test`() {
        assertEquals(
                """|        description = <<
                   |            This test plan will do wonderful things.
                   |            It will surpass your wildest expectations.
                   |        >>
                   |
                   |        created-date-utc = 1900-01-02T03:04:05.000000006
                   |
                   |        finalized-date-utc = 2000-10-20T03:40:50.000000060
                   |""".trimMargin(),
                FileManualTestPlanSerializer.serializeToString(
                        FileManualTestPlan(
                                description = """ |This test plan will do wonderful things.
                                                  |It will surpass your wildest expectations.""".trimMargin(),
                                createdDateUtc = LocalDateTime.of(1900, 1, 2, 3, 4, 5, 6),
                                finalizedDateUtc = LocalDateTime.of(2000, 10, 20, 3, 40, 50, 60)
                        ),
                        2
                )
        )
    }

}