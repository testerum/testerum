package com.testerum.file_service.file

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common_kotlin.*
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.mapper.business_to_file.BusinessToFileTestMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessTestMapper
import com.testerum.model.exception.ValidationException
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

    fun moveFeatureOrTest(sourcePath: Path, destinationPath: Path, testsDir: JavaPath): Path {
        if (sourcePath.isEmpty()) {
            throw ValidationException("You are not allowed to move the root")
        }

        val escapedSourceFile = sourcePath.escape()
        val escapedDestinationPath = destinationPath.escape()

        val escapedDestinationFile = if (escapedDestinationPath.isFile()) {
            escapedDestinationPath
        } else {
            if (escapedSourceFile.isFile()) {
                escapedDestinationPath.copy(
                        directories = escapedDestinationPath.directories,
                        fileName = escapedSourceFile.fileName,
                        fileExtension = escapedSourceFile.fileExtension
                )
            } else {
                escapedDestinationPath.copy(
                        directories = escapedDestinationPath.directories + escapedSourceFile.directories.last()
                )
            }
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

        return Path.createInstance(
                testsDir.relativize(destinationJavaFile).toString()
        )
    }

    fun copyFeatureOrTest(sourcePath: Path, destinationPath: Path, testsDir: JavaPath): Path {
        if (sourcePath.isEmpty()) {
            throw ValidationException("You are not allowed to copy the root")
        }

        val escapedSourceFile = sourcePath.escape()
        val escapedDestinationPath = destinationPath.escape()

        val escapedDestinationFile = if (escapedSourceFile.isFile()) {
            escapedDestinationPath.copy(
                    fileName = escapedSourceFile.fileName,
                    fileExtension = escapedSourceFile.fileExtension
            )
        } else {
            escapedDestinationPath.copy(
                    directories = destinationPath.directories + sourcePath.directories.last()
            )
        }

        val sourceJavaFile: JavaPath = testsDir.resolve(escapedSourceFile.toString())

        if (escapedSourceFile != escapedDestinationFile) {
            val destinationJavaFile: JavaPath = testsDir.resolve(escapedDestinationFile.toString())

            sourceJavaFile.smartCopyTo(
                    destinationJavaFile,
                    createDestinationExistsException = {
                        ValidationException("A test with the same file name already exists.")
                    }
            )
            return escapedDestinationFile;

        } else {

            val destinationFileSuffixIfIsNotUnique: String = getDestinationFileSuffixToBeUnique(escapedDestinationFile, testsDir)
            val escapedDestinationPathWithSuffix: Path = appendSuffixToPath(escapedDestinationFile, destinationFileSuffixIfIsNotUnique)

            val fullDestinationJavaPath = testsDir.resolve(escapedDestinationPathWithSuffix.toString())
            sourceJavaFile.smartCopyTo(
                    fullDestinationJavaPath,
                    createDestinationExistsException = {
                        ValidationException("the file at path [$fullDestinationJavaPath] already exists")
                    }
            )

            val savedTest = changeTestNameWithNewSuffix(escapedDestinationPathWithSuffix, destinationFileSuffixIfIsNotUnique, testsDir)
            if (savedTest != null) {
                return savedTest.path;
            }

            return escapedDestinationPathWithSuffix
        }
    }

    private fun changeTestNameWithNewSuffix(destinationJavaFileWithSuffix: Path,
                                            destinationFileSuffixIfIsNotUnique: String,
                                            testsDir: JavaPath): TestModel? {

        if (!destinationJavaFileWithSuffix.isDirectory() && destinationFileSuffixIfIsNotUnique.isNotEmpty()) {
            val test = getTestAtPath(
                    destinationJavaFileWithSuffix,
                    testsDir
            )
            val testWithNewName = test.copy(text = test.text + destinationFileSuffixIfIsNotUnique);
            return save(testWithNewName, testsDir)
        }
        return null;
    }

    private fun getDestinationFileSuffixToBeUnique(escapedDestinationPath: Path, testsDir: JavaPath): String {
        val destinationFullJavaPath = testsDir.resolve(escapedDestinationPath.toString())
        if (!destinationFullJavaPath.exists) {
            return ""
        }

        val destinationFileSuffix = " - Copy"
        var destinationFileSuffixCount = 0;

        var destinationUniqueSuffix: String = "";

        do {
            destinationUniqueSuffix = destinationFileSuffix + (if (destinationFileSuffixCount != 0) "_$destinationFileSuffixCount" else "")
            destinationFileSuffixCount++;

            var destinationPath = escapedDestinationPath
            if (destinationPath.isFile()) {
                destinationPath = Path(
                        destinationPath.directories,
                        destinationPath.fileName + destinationUniqueSuffix,
                        destinationPath.fileExtension
                )
            } else {
                val uniqueDirPath = destinationPath.directories.toMutableList()
                uniqueDirPath[uniqueDirPath.size-1] = uniqueDirPath[uniqueDirPath.size-1] + destinationUniqueSuffix;
                destinationPath = Path(
                        uniqueDirPath
                )
            }

            val destinationUniqueJavaPath = testsDir.resolve(destinationPath.escape().toString());
        } while(destinationUniqueJavaPath.exists)

        return destinationUniqueSuffix;
    }

    private fun appendSuffixToPath(escapedDestinationPath: Path, destinationFileSuffix: String): Path {
        if (destinationFileSuffix.isEmpty()) {
            return escapedDestinationPath;
        }

        var escapedResultPath = escapedDestinationPath
        if (escapedResultPath.isFile()) {
            escapedResultPath = Path(
                    escapedResultPath.directories,
                    escapedResultPath.fileName + destinationFileSuffix,
                    escapedResultPath.fileExtension
            ).escape()
        } else {
            val uniqueDirPath = escapedResultPath.directories.toMutableList()
            uniqueDirPath[uniqueDirPath.size-1] = uniqueDirPath[uniqueDirPath.size-1] + destinationFileSuffix;
            escapedResultPath = Path(
                    uniqueDirPath
            ).escape()
        }

        return escapedResultPath;
    }
}
