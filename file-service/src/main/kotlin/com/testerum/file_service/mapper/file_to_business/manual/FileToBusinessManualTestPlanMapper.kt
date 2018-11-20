package com.testerum.file_service.mapper.file_to_business.manual

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTestPlan
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlan
import java.time.ZoneId
import java.nio.file.Path as JavaPath

class FileToBusinessManualTestPlanMapper {

    fun mapPlan(filePlan: FileManualTestPlan,
                relativeFilePath: JavaPath): ManualTestPlan {
        val path = Path.createInstance(relativeFilePath.toString())

        val currentTimezone: ZoneId = ZoneId.systemDefault()

        return ManualTestPlan(
                name = relativeFilePath.fileName?.toString().orEmpty(),
                path = path,
                description = filePlan.description,
                isFinalized = filePlan.isFinalized,
                createdDate = filePlan.createdDateUtc?.atZone(currentTimezone)?.toLocalDateTime(),
                finalizedDate = filePlan.finalizedDateUtc?.atZone(currentTimezone)?.toLocalDateTime()
        )
    }

}