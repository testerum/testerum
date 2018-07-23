package net.qutester.service.mapper

import com.testerum.test_file_format.testdef.FileTestDef
import net.qutester.model.test.TestModel
import net.testerum.db_file.model.RepositoryFile

open class FileToUiTestMapper(private val fileToUiStepMapper: FileToUiStepMapper) {

    fun mapToUiModel(fileTestDef: FileTestDef, testFile: RepositoryFile): TestModel {

        return TestModel(
                path = testFile.knownPath.asPath(),
                isManual = fileTestDef.isManual,
                text = fileTestDef.name,
                description = fileTestDef.description,
                tags = fileTestDef.tags,
                stepCalls = fileToUiStepMapper.mapStepsCalls(
                        fileTestDef.steps,
                        testFile.knownPath.toString()
                )
        )
    }
}