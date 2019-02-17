package com.testerum.file_service.file

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.serializing.Serializer
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteIfExists
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.getContent
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.smartMoveTo
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualTestMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestMapper
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTest
import com.testerum.test_file_format.manual_test.FileManualTestDef
import com.testerum.test_file_format.manual_test.FileManualTestDefSerializer
import com.testerum.test_file_format.testdef.FileTestDefParserFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class ManualTestFileService(private val businessToFileManualTestMapper: BusinessToFileManualTestMapper,
                            private val fileToBusinessManualTestMapper: FileToBusinessManualTestMapper) {

    companion object {
        private val MANUAL_TEST_PARSER = ParserExecuter(FileTestDefParserFactory.manualTestDef())
        private val MANUAL_TEST_SERIALIZER: Serializer<FileManualTestDef> = FileManualTestDefSerializer
    }

    fun save(test: ManualTest,
             planPath: Path,
             manualTestsDir: JavaPath): ManualTest {
        val oldPath = test.oldPath
        val newEscapedPath = test.getNewPath().escape()

        val planDir = manualTestsDir.resolve(planPath.toString())

        val oldTestFile: JavaPath? = oldPath?.let {
            planDir.resolve(oldPath.toString())
        }
        val newTestFile: JavaPath = planDir.resolve(newEscapedPath.toString())

        // handle rename
        oldTestFile?.smartMoveTo(
                newTestFile,
                createDestinationExistsException = {
                    ValidationException(
                            globalMessage = "The manual test at path [$newEscapedPath] already exists",
                            globalHtmlMessage = "The manual test at path<br/><code>$newEscapedPath</code><br/>already exists"
                    )
                }
        )

        // write the new test file
        newTestFile.parent?.createDirectories()
        val fileTest = businessToFileManualTestMapper.mapTest(test)
        val serializedFileTest = MANUAL_TEST_SERIALIZER.serializeToString(fileTest)

        newTestFile.parent?.createDirectories()

        Files.write(
                newTestFile,
                serializedFileTest.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        val newPath = Path.createInstance(
                planDir.relativize(newTestFile).toString()
        ).escape()

        return getTestAtPath(planPath, newPath, manualTestsDir)!!
    }

    fun getTestAtPath(planPath: Path,
                      testPath: Path,
                      manualTestsDir: java.nio.file.Path): ManualTest? {
        val escapedTestPath = testPath.escape()
        val escapedPlanPath = planPath.escape()

        val planJavaPath = manualTestsDir.resolve(escapedPlanPath.toString())
        val testFileJavaPath = planJavaPath.resolve(escapedTestPath.toString())

        return getTestAtJavaPath(testFileJavaPath, planJavaPath)
    }

    private fun getTestAtJavaPath(testFileJavaPath: JavaPath, planJavaPath: JavaPath): ManualTest? {
        if (testFileJavaPath.doesNotExist) {
            return null
        }

        val fileTest = MANUAL_TEST_PARSER.parse(
                testFileJavaPath.getContent()
        )

        val absoluteTestsDir = planJavaPath.toAbsolutePath().normalize()
        val relativePath = absoluteTestsDir.relativize(testFileJavaPath)
        val test = fileToBusinessManualTestMapper.mapTest(fileTest, relativePath)

        return test
    }

    fun getTestsAtPlanPath(planPath: Path, manualTestsDir: JavaPath): List<ManualTest> {
        val escapedPlanPath = planPath.escape()

        val planJavaPath = manualTestsDir.resolve(
                escapedPlanPath.toString()
        )

        val testsJavaPaths = planJavaPath.walkAndCollect { it.hasExtension(".${ManualTest.MANUAL_TEST_FILE_EXTENSION}") }

        val tests = testsJavaPaths.map { getTestAtJavaPath(it, planJavaPath) }
                .filterNotNull()

        return tests
    }

    fun deleteTestAtPath(testPath: Path, planPath: Path, manualTestsDir: JavaPath) {
        val escapedTestPath = testPath.escape()
        val escapedPlanPath = planPath.escape()

        val planJavaPath = manualTestsDir.resolve(escapedPlanPath.toString())
        val testFileJavaPath = planJavaPath.resolve(escapedTestPath.toString())

        testFileJavaPath.deleteIfExists()
    }

}
