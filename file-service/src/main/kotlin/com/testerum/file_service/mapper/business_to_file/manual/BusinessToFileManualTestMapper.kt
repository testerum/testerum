package com.testerum.file_service.mapper.business_to_file.manual

import com.testerum.model.manual.ManualTest
import com.testerum.test_file_format.manual_test.FileManualTestDef

class BusinessToFileManualTestMapper(private val manualStepCallMapper: BusinessToFileManualStepCallMapper,
                                     private val manualTestStatusMapper: BusinessToFileManualTestStatusMapper) {

    fun mapTest(manualTest: ManualTest): FileManualTestDef {
        return FileManualTestDef(
                name = manualTest.name,
                description = manualTest.description,
                tags = manualTest.tags,
                stepCalls = manualStepCallMapper.mapStepCalls(manualTest.stepCalls),
                status = manualTestStatusMapper.mapStatus(manualTest.status),
                comments = manualTest.comments
        )
    }
}