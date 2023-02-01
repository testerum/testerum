package com.testerum.file_service.mapper.file_to_business.manual

import com.testerum.common_kotlin.utcToLocalTimeZone
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTestPlan
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlan
import java.nio.file.Path as JavaPath

class FileToBusinessManualTestPlanMapper {

    fun mapPlan(filePlan: FileManualTestPlan,
                relativeFilePath: JavaPath): ManualTestPlan {
        val path = Path.createInstance(relativeFilePath.toString())

        return ManualTestPlan(
                name = relativeFilePath.fileName?.toString().orEmpty(),
                path = path,
                description = filePlan.description,
                isFinalized = filePlan.isFinalized,
                createdDate = filePlan.createdDateUtc?.utcToLocalTimeZone(),
                finalizedDate = filePlan.finalizedDateUtc?.utcToLocalTimeZone()
        )
    }

}