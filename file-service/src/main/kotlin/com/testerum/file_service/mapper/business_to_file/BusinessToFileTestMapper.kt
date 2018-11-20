package com.testerum.file_service.mapper.business_to_file

import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.model.test.TestModel
import com.testerum.model.test.TestProperties
import com.testerum.test_file_format.testdef.FileTestDef
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties

class BusinessToFileTestMapper(private val businessToFileStepCallMapper: BusinessToFileStepCallMapper) {

    fun map(test: TestModel): FileTestDef {
        return FileTestDef(
                name = test.name,
                properties = mapTestProperties(test.properties),
                description = test.description,
                tags = test.tags,
                steps = businessToFileStepCallMapper.mapStepCalls(test.stepCalls)
        )
    }

    private fun mapTestProperties(properties: TestProperties): FileTestDefProperties {
        return FileTestDefProperties(
                isManual = properties.isManual,
                isDisabled = properties.isDisabled
        )
    }

}
