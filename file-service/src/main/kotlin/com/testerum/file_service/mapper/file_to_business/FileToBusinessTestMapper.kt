package com.testerum.file_service.mapper.file_to_business

import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.model.test.TestProperties
import com.testerum.test_file_format.testdef.FileTestDef
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import java.nio.file.Path as JavaPath

class FileToBusinessTestMapper(private val stepCallMapper: FileToBusinessStepCallMapper) {

    fun mapTest(fileTest: FileTestDef, relativeFilePath: JavaPath): TestModel {
        val path = Path.createInstance(relativeFilePath.toString())

        val stepCalls = stepCallMapper.mapStepsCalls(
                fileStepCalls = fileTest.steps,
                stepCallIdPrefix = path.toString()
        )

        return TestModel(
                name = fileTest.name,
                path = path,
                properties = mapTestProperties(fileTest.properties),
                description = fileTest.description,
                tags = fileTest.tags,
                stepCalls = stepCalls
        )
    }

    private fun mapTestProperties(properties: FileTestDefProperties) = TestProperties(
            isManual = properties.isManual,
            isDisabled = properties.isDisabled
    )

}
