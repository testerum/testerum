package net.qutester.service.manual

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.file_repository.model.RepositoryFile
import com.testerum.file_repository.model.RepositoryFileChange
import net.qutester.common.json.ObjectMapperFactory
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.enums.ManualTestStatus
import net.qutester.model.manual.runner.ManualTestExe
import net.qutester.model.manual.runner.ManualTestsRunner
import net.qutester.model.manual.runner.enums.ManualTestsRunnerStatus
import net.qutester.model.manual.runner.operation.UpdateManualTestExecutionModel
import net.qutester.model.manual.runner.operation.UpdateManualTestsRunnerModel
import net.qutester.model.repository.enums.FileType
import java.time.LocalDateTime


class ManualTestsRunnerService(private val fileRepositoryService: FileRepositoryService) {

    val objectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapper()

    fun createTest(manualTestsRunner: ManualTestsRunner): ManualTestsRunner {
        val manualTestsRunnerToSave = manualTestsRunner.copy(createdDate = LocalDateTime.now())

        val fileName = getFileName(manualTestsRunnerToSave)

        val testPath = Path(manualTestsRunnerToSave.path.directories, fileName, FileType.MANUAL_TESTS_RUNNER.fileExtension)

        val fileTestAsString = objectMapper.writeValueAsString(manualTestsRunnerToSave)

        val createdRepositoryFile = fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(testPath, FileType.MANUAL_TESTS_RUNNER),
                                fileTestAsString
                        )
                )
        )

          return manualTestsRunnerToSave.copy(
                path = createdRepositoryFile.knownPath.asPath()
        )
    }

    private fun getFileName(manualTestsRunner: ManualTestsRunner): String {
        var fileName = if (manualTestsRunner.environment != null) manualTestsRunner.environment + "_" else ""
        fileName += if (manualTestsRunner.applicationVersion != null) manualTestsRunner.applicationVersion else ""
        return fileName
    }

    fun updateTest(updateManualTestsRunnerModel: UpdateManualTestsRunnerModel): ManualTestsRunner {
        val resolvedManualTestsRunner = resolveRunnerWithCurrentStateFromFile(updateManualTestsRunnerModel)

        val oldPath = updateManualTestsRunnerModel.oldPath

        val fileName = getFileName(updateManualTestsRunnerModel.manualTestsRunner)
        val newPath = Path(resolvedManualTestsRunner.path.directories, fileName, FileType.MANUAL_TESTS_RUNNER.fileExtension)

        val fileTestAsString = objectMapper.writeValueAsString(resolvedManualTestsRunner)

        val repositoryFile = fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(oldPath, FileType.MANUAL_TESTS_RUNNER),
                        RepositoryFile(
                                KnownPath(newPath, FileType.MANUAL_TESTS_RUNNER),
                                fileTestAsString
                        )
                )
        )

        val resolvedTestModel = resolvedManualTestsRunner.copy(
                path = newPath
        )

        return resolveManualTestsRunner(resolvedTestModel, repositoryFile)
    }

    private fun resolveRunnerWithCurrentStateFromFile(updateManualTestsRunnerModel: UpdateManualTestsRunnerModel): ManualTestsRunner {
        val manualTestsRunner = updateManualTestsRunnerModel.manualTestsRunner

        val resolvedTests: MutableList<ManualTestExe> = mutableListOf()

        val testsToExecute = manualTestsRunner.testsToExecute
        val oldTestRunner = getTestsRunnerAtPath(updateManualTestsRunnerModel.oldPath)?: return manualTestsRunner
        val oldTestToExecute = oldTestRunner.testsToExecute
        for (test in testsToExecute) {
            val oldTest = oldTestToExecute.find { it.path == test.path }
            if (oldTest == null) {
                resolvedTests.add(test)
            } else {
                resolvedTests.add(oldTest)
            }
        }
        return manualTestsRunner.copy(testsToExecute = resolvedTests)
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(knownPath = KnownPath(path, FileType.MANUAL_TESTS_RUNNER))
    }

    fun finalize(path: Path): ManualTestsRunner {
        val testsRunner = getTestsRunnerAtPath(path)?: throw RuntimeException("The Test Runner [$path] couldn't be found")

        val updatedTestsRunner = testsRunner.copy(
                status = ManualTestsRunnerStatus.FINISHED,
                finalizedDate = LocalDateTime.now()
        )

        val testRunnerAsString = objectMapper.writeValueAsString(updatedTestsRunner)

        val repositoryFile = fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(path, FileType.MANUAL_TESTS_RUNNER),
                        RepositoryFile(
                                KnownPath(path, FileType.MANUAL_TESTS_RUNNER),
                                testRunnerAsString
                        )
                )
        )

        return resolveManualTestsRunner(updatedTestsRunner, repositoryFile)
    }

    fun bringBackInExecution(path: Path): ManualTestsRunner {
        val testsRunner = getTestsRunnerAtPath(path)?: throw RuntimeException("The Test Runner [$path] couldn't be found")

        val updatedTestsRunner = testsRunner.copy(
                status = ManualTestsRunnerStatus.IN_EXECUTION,
                finalizedDate = null
        )

        val testRunnerAsString = objectMapper.writeValueAsString(updatedTestsRunner)

        val repositoryFile = fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(path, FileType.MANUAL_TESTS_RUNNER),
                        RepositoryFile(
                                KnownPath(path, FileType.MANUAL_TESTS_RUNNER),
                                testRunnerAsString
                        )
                )
        )

        return resolveManualTestsRunner(updatedTestsRunner, repositoryFile)
    }

    fun getAllTestsRunners(): List<ManualTestsRunner> {

        val runners = mutableListOf<ManualTestsRunner>()

        val allTestFiles = fileRepositoryService.getAllResourcesByType(FileType.MANUAL_TESTS_RUNNER)
        for (testFile in allTestFiles) {
            val manualTest = objectMapper.readValue<ManualTestsRunner>(testFile.body)

            val resolvedManualTest = resolveManualTestsRunner(manualTest, testFile)
            val resolvedWithoutTests = resolvedManualTest.copy(testsToExecute = emptyList())
            runners.add(resolvedWithoutTests)
        }

        return runners
    }

    fun getTestsRunnerAtPath(path: Path): ManualTestsRunner? {
        val testFile = fileRepositoryService.getByPath(
                KnownPath(path, FileType.MANUAL_TESTS_RUNNER)
        ) ?: return null

        val manualTest = objectMapper.readValue<ManualTestsRunner>(testFile.body)

        return resolveManualTestsRunner(manualTest, testFile)
    }

    private fun resolveManualTestsRunner(manualTest: ManualTestsRunner, testFile: RepositoryFile): ManualTestsRunner {
        var totalTests = 0
        var passedTests = 0
        var failedTests = 0
        var blockedTests = 0
        var notApplicableTests = 0
        var notExecutedTests = 0

        for (test in manualTest.testsToExecute) {
            totalTests++

            when (test.testStatus) {
                ManualTestStatus.PASSED -> passedTests++
                ManualTestStatus.FAILED -> failedTests++
                ManualTestStatus.BLOCKED -> blockedTests++
                ManualTestStatus.NOT_APPLICABLE -> notApplicableTests++
                ManualTestStatus.NOT_EXECUTED -> notExecutedTests++
                ManualTestStatus.IN_PROGRESS -> notExecutedTests++
            }
        }

        return manualTest.copy(
                path = testFile.knownPath.asPath(),
                totalTests = totalTests,
                passedTests = passedTests,
                failedTests = failedTests,
                blockedTests = blockedTests,
                notApplicableTests = notApplicableTests,
                notExecutedTests = notExecutedTests
        )
    }

    fun updateTestExecution(updateManualTestExecutionModel: UpdateManualTestExecutionModel): ManualTestsRunner? {
        val oldTestsRunner = getTestsRunnerAtPath(updateManualTestExecutionModel.manualTestRunnerPath)?: return null
        val newTestsToExecute = oldTestsRunner.testsToExecute.toMutableList()
        val oldTestInRunner = newTestsToExecute.first { it.path == updateManualTestExecutionModel.manualTest.path }

        newTestsToExecute[newTestsToExecute.indexOf(oldTestInRunner)] = updateManualTestExecutionModel.manualTest

        val newTestsRunner = oldTestsRunner.copy(
                testsToExecute = newTestsToExecute
        )

        val fileTestAsString = objectMapper.writeValueAsString(newTestsRunner)
        val testsRunnerKnownPath = KnownPath(updateManualTestExecutionModel.manualTestRunnerPath, FileType.MANUAL_TESTS_RUNNER)
        fileRepositoryService.update(
                RepositoryFileChange(
                        testsRunnerKnownPath,
                        RepositoryFile(
                                testsRunnerKnownPath,
                                fileTestAsString
                        )
                )
        )

        return newTestsRunner
    }
}