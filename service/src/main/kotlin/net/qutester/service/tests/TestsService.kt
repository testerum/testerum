package net.qutester.service.tests

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.testdef.FileTestDef
import com.testerum.test_file_format.testdef.FileTestDefParserFactory
import com.testerum.test_file_format.testdef.FileTestDefSerializer
import net.qutester.model.arg.Arg
import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.operation.UpdateTestModel
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.model.step.StepCall
import net.qutester.model.test.TestModel
import net.qutester.service.mapper.FileToUiTestMapper
import net.qutester.service.mapper.UiToFileTestMapper
import net.qutester.service.resources.ResourcesService
import net.qutester.service.tests.resolver.TestResolver
import net.qutester.service.warning.WarningService
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange
import java.io.StringWriter


class TestsService(private val testResolver: TestResolver,
                   private val fileRepositoryService: FileRepositoryService,
                   private val resourcesService: ResourcesService,
                   private val uiToFileTestMapper: UiToFileTestMapper,
                   private val fileToUiTestMapper: FileToUiTestMapper,
                   private val warningService: WarningService) {

    companion object {
        private val TEST_PARSER = ParserExecuter(FileTestDefParserFactory.testDef())
    }

    fun createTest(testModel: TestModel): TestModel {
        val testModelWithCorrectlyNamedExternalResources: TestModel = saveExternalResources(testModel)

        val testPath = Path(testModelWithCorrectlyNamedExternalResources.path.directories, testModelWithCorrectlyNamedExternalResources.text, FileType.TEST.fileExtension)
        val fileTest = uiToFileTestMapper.mapToFileModel(testModelWithCorrectlyNamedExternalResources)

        val destination = StringWriter()
        FileTestDefSerializer.serialize(fileTest, destination, 0)
        val fileTestAsString = destination.toString()

        val createdRepositoryFile = fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(testPath, FileType.TEST),
                                fileTestAsString
                        )
                )
        )

        val resolvedUiTestWithWarnings = getTestAtPath(
                path = createdRepositoryFile.knownPath.asPath()
        )

        return resolvedUiTestWithWarnings!!
    }

    fun updateTest(updateTestModel: UpdateTestModel): TestModel {
        val testModel: TestModel = updateTestModel.testModel

        val testModelWithCorrectlyNamedExternalResources: TestModel = saveExternalResources(testModel)

        val oldPath: Path = updateTestModel.oldPath
        val newPath = Path(testModelWithCorrectlyNamedExternalResources.path.directories, testModelWithCorrectlyNamedExternalResources.text, FileType.TEST.fileExtension)

        val fileTest: FileTestDef = uiToFileTestMapper.mapToFileModel(testModelWithCorrectlyNamedExternalResources)

        val destination = StringWriter()
        FileTestDefSerializer.serialize(fileTest, destination, 0)
        val fileTestAsString = destination.toString()

        fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(oldPath, FileType.TEST),
                        RepositoryFile(
                                KnownPath(newPath, FileType.TEST),
                                fileTestAsString
                        )
                )
        )

        val resolvedUiTestWithWarnings = getTestAtPath(
                path = newPath
        )

        return resolvedUiTestWithWarnings!!
    }

    private fun saveExternalResources(testModel: TestModel): TestModel {
        val stepCalls: List<StepCall> = testModel.stepCalls.map { stepCall ->
            saveExternalResources(stepCall)
        }
        
        return testModel.copy(stepCalls = stepCalls)
    }

    private fun saveExternalResources(stepCall: StepCall): StepCall {
        val argsWithNewNames: List<Arg> = stepCall.args.map { arg ->
            saveExternalResource(arg)
        }

        return stepCall.copy(args = argsWithNewNames)
    }

    /**
     * if Arg does not represent an external resource, do nothing;
     * otherwise save it and return the new name
     */
    private fun saveExternalResource(arg: Arg): Arg {
        // todo: don't save if the content didn't change?
        val path: Path = arg.path
                ?: return arg // if we don't have a path, then this is an internal resource

        val resourceContext: ResourceContext = resourcesService.save(
                ResourceContext(
                        oldPath = path,
                        path = path,
                        body = arg.content.orEmpty()
                )
        )

        val actualPath: Path = resourceContext.path

        return arg.copy(path = actualPath)
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
            val resolvedUiTest = testResolver.resolveComposedSteps(
                    unresolvedUiTest,
                    throwExceptionOnNotFound = false
            )
            val resolvedUiTestWithWarnings: TestModel = warningService.testWithWarnings(resolvedUiTest)

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
        val resolvedUiTest = testResolver.resolveComposedSteps(unresolvedUiTest, throwExceptionOnNotFound = false)
        val resolvedUiTestWithWarnings: TestModel = warningService.testWithWarnings(resolvedUiTest)

        return resolvedUiTestWithWarnings
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

}