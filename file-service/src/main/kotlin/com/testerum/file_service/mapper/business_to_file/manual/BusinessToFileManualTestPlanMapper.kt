package com.testerum.file_service.mapper.business_to_file.manual

import com.testerum.model.manual.ManualTestPlan
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlan
import java.time.LocalDateTime
import java.time.ZoneId

class BusinessToFileManualTestPlanMapper {

    companion object {
        private val UTC_TIMEZONE = ZoneId.of("UTC")
    }

    fun mapPlan(manualTestPlan: ManualTestPlan): FileManualTestPlan {
        return FileManualTestPlan(
                description = manualTestPlan.description,
                createdDateUtc = manualTestPlan.createdDate ?: LocalDateTime.now(UTC_TIMEZONE),
                isFinalized = manualTestPlan.isFinalized,
                finalizedDateUtc = manualTestPlan.finalizedDate
        )
    }

}
