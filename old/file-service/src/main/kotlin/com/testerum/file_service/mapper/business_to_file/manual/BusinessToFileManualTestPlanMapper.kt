package com.testerum.file_service.mapper.business_to_file.manual

import com.testerum.common_kotlin.localToUtcTimeZone
import com.testerum.common_kotlin.utcCurrentLocalDateTime
import com.testerum.model.manual.ManualTestPlan
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlan

class BusinessToFileManualTestPlanMapper {

    fun mapPlan(manualTestPlan: ManualTestPlan): FileManualTestPlan {
        return FileManualTestPlan(
                description = manualTestPlan.description,
                createdDateUtc = manualTestPlan.createdDate?.localToUtcTimeZone() ?: utcCurrentLocalDateTime(),
                isFinalized = manualTestPlan.isFinalized,
                finalizedDateUtc = manualTestPlan.finalizedDate?.localToUtcTimeZone()
        )
    }

}
