package com.testerum.test_file_format.manual_test_plan

import com.testerum.common.parsing.executer.ParserExecuter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FileManualTestPlanParserFactoryTest {

    private val parser = ParserExecuter(
            FileManualTestPlanParserFactory.manualTestPlan()
    )

    @Test
    fun `test everything`() {
        assertThat(
                parser.parse(
                        """|description = <<
                           |    This test plan will do wonderful things.
                           |    It will surpass your wildest expectations.
                           |>>
                           |
                           |created-date-utc = 1900-01-02 03:04:05
                           |
                           |is-finalized = true
                           |
                           |finalized-date-utc = 2000-10-20 03:40:50
                           |""".trimMargin()
                ),
                equalTo(
                        FileManualTestPlan(
                                description = """ |This test plan will do wonderful things.
                                          |It will surpass your wildest expectations.""".trimMargin(),
                                createdDateUtc = LocalDateTime.of(1900, 1, 2, 3, 4, 5, 0),
                                finalizedDateUtc = LocalDateTime.of(2000, 10, 20, 3, 40, 50, 0),
                                isFinalized = true
                        )
                )
        )
    }

}