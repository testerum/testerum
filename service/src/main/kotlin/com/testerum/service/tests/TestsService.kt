package com.testerum.service.tests

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.model.test.TestModel
import com.testerum.service.mapper.FileToUiTestMapper
import com.testerum.service.tests.resolver.TestResolver
import com.testerum.service.warning.WarningService
import com.testerum.test_file_format.testdef.FileTestDefParserFactory


class TestsService(private val testResolver: TestResolver,
                   private val fileRepositoryService: FileRepositoryService,
                   private val fileToUiTestMapper: FileToUiTestMapper,
                   private val warningService: WarningService) {

    companion object {
        private val TEST_PARSER = ParserExecuter(FileTestDefParserFactory.testDef())
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(knownPath = KnownPath(path, FileType.TEST))
    }

    fun getAllTests(): List<TestModel> {
        return getTestsUnderPath(Path.EMPTY)
    }

    fun getTestsUnderPath(path: Path): List<TestModel> {
        val uiTests = mutableListOf<TestModel>()

        val allTestFiles = fileRepositoryService.getAllResourcesByTypeUnderPath(KnownPath(path, FileType.TEST))
        for (testFile in allTestFiles) {
            val fileTest = TEST_PARSER.parse(testFile.body)
            val unresolvedUiTest = fileToUiTestMapper.mapToUiModel(fileTest, testFile)
            val resolvedUiTest = testResolver.resolveSteps(
                    unresolvedUiTest,
                    throwExceptionOnNotFound = false
            )
            val resolvedUiTestWithWarnings: TestModel = warningService.testWithWarnings(resolvedUiTest, keepExistingWarnings = true)

            uiTests.add(resolvedUiTestWithWarnings)
        }

        return uiTests
    }

    fun getTestAtPath(path: Path): TestModel? {
        val testFile = fileRepositoryService.getByPath(
                KnownPath(path, FileType.TEST)
        ) ?: return null

        val fileTest = TEST_PARSER.parse(testFile.body)
        val unresolvedUiTest = fileToUiTestMapper.mapToUiModel(fileTestDef = fileTest, testFile = testFile)
        val resolvedUiTest = testResolver.resolveSteps(unresolvedUiTest, throwExceptionOnNotFound = false)

        return warningService.testWithWarnings(resolvedUiTest, keepExistingWarnings = true)
    }

    fun getTestsForPath(testOrDirectoryPaths: List<Path>): List<TestModel> {
        val tests = mutableListOf<TestModel>()

        val (testPaths, directoryPaths) = testOrDirectoryPaths.partition { it.fileExtension == FileType.TEST.fileExtension }

        for (testPath in testPaths) {
            val testAtPath: TestModel = getTestAtPath(testPath)
                    ?: continue

            tests += testAtPath
        }

        for (directoryPath in directoryPaths) {
            val testsUnderPath: List<TestModel> = getTestsUnderPath(directoryPath)

            tests += testsUnderPath
        }

        return tests
    }

    fun deleteDirectory(path: Path) {
        return fileRepositoryService.delete(
                KnownPath(path, FileType.TEST)
        )
    }

    fun moveDirectoryOrFile(copyPath: CopyPath) {
        fileRepositoryService.moveDirectoryOrFile(
                KnownPath(copyPath.copyPath, FileType.TEST),
                KnownPath(copyPath.destinationPath, FileType.TEST)
        )
    }

    fun getWarnings(testModel: TestModel, keepExistingWarnings: Boolean): TestModel
            = warningService.testWithWarnings(testModel, keepExistingWarnings)

}
