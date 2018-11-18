package com.testerum.test_file_format.manual_test_plan

import java.time.LocalDateTime

data class FileManualTestPlan(val description: String?,
                              val createdDateUtc: LocalDateTime?,
                              val finalizedDateUtc: LocalDateTime?)
