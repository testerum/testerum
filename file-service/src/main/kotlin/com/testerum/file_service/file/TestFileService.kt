package com.testerum.file_service.file

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteIfExists
import com.testerum.common_kotlin.getContent
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.smartMoveTo
import com.testerum.common_kotlin.walk
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.mapper.business_to_file.BusinessToFileTestMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessTestMapper
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.model.test.TestModel.Companion.TEST_FILE_EXTENSION
import com.testerum.test_file_format.testdef.FileTestDef
import com.testerum.test_file_format.testdef.FileTestDefParserFactory
import com.testerum.test_file_format.testdef.FileTestDefSerializer
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class TestFileService(private val fileToBusinessTestMapper: FileToBusinessTestMapper,
                      private val businessToFileTestMapper: BusinessToFileTestMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TestFileService::class.java)

        private val TEST_PARSER = ParserExecuter(FileTestDefParserFactory.testDef())
        private val TEST_SERIALIZER = FileTestDefSerializer
    }

    fun getAllTests(testsDir: JavaPath): List<TestModel> {
        val tests = mutableListOf<TestModel>()

        val absoluteTestsDir = testsDir.toAbsolutePath().normalize()
        absoluteTestsDir.walk { path ->
            if (path.isTest) {
                val fileTest = parseTestFileSafely(path)

                if (fileTest != null) {
                    val relativePath = absoluteTestsDir.relativize(path)
                    val test = fileToBusinessTestMapper.mapTest(fileTest, relativePath)

                    tests += test
                }
            }
        }

        return tests
    }

    private fun getTestAtPath(path: Path,
                              testsDir: JavaPath): TestModel {
        val escapedPath = path.escape()

        val testFile = testsDir.resolve(
                escapedPath.toString()
        )

        val fileTest = parseTestFile(testFile)

        val absoluteTestsDir = testsDir.toAbsolutePath().normalize()
        val relativePath = absoluteTestsDir.relativize(testFile)
        val test = fileToBusinessTestMapper.mapTest(fileTest, relativePath)

        return test
    }

    private val JavaPath.isTest: Boolean
        get() = isRegularFile && hasExtension(".$TEST_FILE_EXTENSION")


    private fun parseTestFileSafely(file: JavaPath): FileTestDef? {
        return try {
            parseTestFile(file)
        } catch (e: Exception) {
            LOG.warn("failed to load test at [${file.toAbsolutePath().normalize()}]", e)

            null
        }
    }

    private fun parseTestFile(file: JavaPath): FileTestDef {
        try {
            return TEST_PARSER.parse(
                    file.getContent()
            )
        } catch (e: Exception) {
            throw RuntimeException("failed to parse test at [${file.toAbsolutePath().normalize()}]", e)
        }
    }

    fun save(test: TestModel, testsDir: JavaPath): TestModel {
        val oldEscapedPath = test.oldPath?.escape()
        val newEscapedPath = test.getNewPath().escape()

        val oldTestFile: JavaPath? = oldEscapedPath?.let {
            testsDir.resolve(oldEscapedPath.toString())
        }
        val newTestFile: JavaPath = testsDir.resolve(newEscapedPath.toString())

        // handle rename
        oldTestFile?.smartMoveTo(
                newTestFile,
                createDestinationExistsException = {
                    ValidationException("the test at path [$newEscapedPath] already exists")
                }
        )

        // write the new test file
        newTestFile.parent?.createDirectories()
        val fileTest = businessToFileTestMapper.map(test)
        val serializedFileTest = TEST_SERIALIZER.serializeToString(fileTest)

        newTestFile.parent?.createDirectories()

        Files.write(
                newTestFile,
                serializedFileTest.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        val newPath = Path.createInstance(
                testsDir.relativize(newTestFile).toString()
        ).escape()

        return getTestAtPath(newPath, testsDir)
    }

    fun deleteTest(path: Path, testsDir: JavaPath) {
        val escapedPath = path.escape()

        val testFile = testsDir.resolve(escapedPath.toString())
        testFile.deleteIfExists()
    }

    /**
     * * ``copyPath.copyPath`` MUST BE a file
     * * if ``copyPath.destinationKnownPath`` is a file, this method renames ``copyPath`` to this new name
     * * if ``copyPath.destinationKnownPath`` is a directory, this method moves ``copyPath`` inside this directory
     *
     * @return the destination file name, with escapes applied
     */
    fun moveTestDirectoryOrFile(copyPath: CopyPath, testsDir: JavaPath): Path {
        val escapedSourceFile = copyPath.copyPath.escape()
        val escapedDestinationPath = copyPath.destinationPath.escape()

        val escapedDestinationFile = if (escapedDestinationPath.isFile()) {
            escapedDestinationPath
        } else {
            escapedDestinationPath.copy(
                    fileName = escapedSourceFile.fileName,
                    fileExtension = escapedSourceFile.fileExtension
            )
        }

        val sourceJavaFile = testsDir.resolve(
                escapedSourceFile.toString()
        )
        val destinationJavaFile = testsDir.resolve(
                escapedDestinationFile.toString()
        )

        sourceJavaFile.smartMoveTo(
                destinationJavaFile,
                createDestinationExistsException = {
                    ValidationException("the file at path [$destinationJavaFile] already exists")
                }
        )

        return escapedDestinationFile
    }

}
