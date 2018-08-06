package com.testerum.service.mapper

import com.testerum.file_repository.model.RepositoryFile
import com.testerum.model.test.TestModel
import com.testerum.model.test.TestProperties
import com.testerum.test_file_format.testdef.FileTestDef
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties

open class FileToUiTestMapper(private val fileToUiStepMapper: FileToUiStepMapper) {

    fun mapToUiModel(fileTestDef: FileTestDef, testFile: RepositoryFile): TestModel {

        return TestModel(
                path = testFile.knownPath.asPath(),
                properties = mapTestProperties(fileTestDef.properties),
                text = fileTestDef.name,
                description = fileTestDef.description,
                tags = fileTestDef.tags,
                stepCalls = fileToUiStepMapper.mapStepsCalls(
                        fileTestDef.steps,
                        testFile.knownPath.toString()
                )
        )
    }

    private fun mapTestProperties(properties: FileTestDefProperties) = TestProperties(
            isManual = properties.isManual,
            isDisabled = properties.isDisabled
    )

}