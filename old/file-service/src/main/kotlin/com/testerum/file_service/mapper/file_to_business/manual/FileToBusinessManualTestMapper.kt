package com.testerum.file_service.mapper.file_to_business.manual

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTest
import com.testerum.test_file_format.manual_test.FileManualTestDef
import java.nio.file.Path as JavaPath

class FileToBusinessManualTestMapper(private val testStatusMapper: FileToBusinessManualTestStatusMapper,
                                     private val stepCallMapper: FileToBusinessManualStepCallMapper) {

    fun mapTest(fileTest: FileManualTestDef, relativeFilePath: JavaPath): ManualTest {
        val path = Path.createInstance(relativeFilePath.toString())

        val stepCalls = stepCallMapper.mapStepCalls(
                fileManualStepCalls = fileTest.stepCalls,
                stepCallIdPrefix = path.toString()
        )

        return ManualTest(
                path = path,
                name = fileTest.name,
                description = fileTest.description,
                tags = fileTest.tags,
                stepCalls = stepCalls,
                status = testStatusMapper.mapStatus(fileTest.status),
                comments = fileTest.comments,
                isFinalized = false // will be filled-in from plan.isFinalized
        )
    }

}