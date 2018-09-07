package com.testerum.service.tests_runner.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.model.runner.tree.RunnerRootNode
import com.testerum.model.runner.tree.builder.RunnerTreeBuilder
import com.testerum.runner.events.model.RunnerErrorEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.exit_code.ExitCode
import com.testerum.service.tests.TestsService
import com.testerum.service.tests_runner.execution.model.RunningTestExecution
import com.testerum.service.tests_runner.execution.model.TestExecution
import com.testerum.service.tests_runner.execution.model.TestExecutionResponse
import com.testerum.service.tests_runner.execution.stopper.ProcessKillerTestExecutionStopper
import com.testerum.settings.SettingsManager
import com.testerum.settings.getRequiredSetting
import com.testerum.settings.getValue
import com.testerum.settings.keys.SystemSettingKeys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class TestsExecutionService(private val testsService: TestsService,
                            private val settingsManager: SettingsManager,
                            private val jsonObjectMapper: ObjectMapper) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TestsExecutionService::class.java)

        private val OS_NAME: String = System.getProperty("os.name").toLowerCase()
        private val IS_WINDOWS: Boolean = OS_NAME.startsWith("win")
    }

    private class TestExecutionIdGenerator {
        private val lastId = AtomicLong(0)

        fun nextId() = lastId.incrementAndGet()
    }

    private val testExecutionIdGenerator = TestExecutionIdGenerator()

    private val testExecutionsById: MutableMap<Long, TestExecution> = ConcurrentHashMap()

    fun createExecution(testOrDirectoryPaths: List<Path>): TestExecutionResponse {
        val executionId = testExecutionIdGenerator.nextId()

        testExecutionsById[executionId] = TestExecution(executionId, testOrDirectoryPaths)

        return TestExecutionResponse(
                executionId = executionId,
                runnerRootNode = getRunnerRootNode(testOrDirectoryPaths)
        )
    }

    private fun getRunnerRootNode(testOrDirectoryPaths: List<Path>): RunnerRootNode {
        val tests = testsService.getTestsForPaths(testOrDirectoryPaths)

        val builder = RunnerTreeBuilder()
        tests.forEach { builder.addTest(it) }

        return builder.build()
    }

    fun stopExecution(executionId: Long) {
        val execution = testExecutionsById[executionId]
        if (execution == null) {
            LOGGER.warn("trying to stop an execution that no longer exists")
            return
        }
        if (execution !is RunningTestExecution) {
            LOGGER.warn("trying to stop an execution that didn't start")
            return
        }

        execution.stopper.stopExecution()
        testExecutionsById.remove(executionId)
    }

    fun startExecution(executionId: Long,
                       eventProcessor: (event: RunnerEvent) -> Unit,
                       doneProcessor: () -> Unit) {
        val execution = testExecutionsById[executionId]
        if (execution == null) {
            LOGGER.warn("trying to start an execution that no longer exists")
            return
        }
        if (execution is RunningTestExecution) {
            LOGGER.warn("trying to start an execution that is already started")
            return
        }

        LOGGER.debug("==========================================[ start test execution {} ]=========================================", executionId)

        val args = createArgs(execution.testOrDirectoryPathsToRun)
        val argsFile: java.nio.file.Path = createArgsFile(args)
        val commandLine: List<String> = createCommandLine(argsFile)

        try {

            val testRunnerEventProcessor = TestRunnerEventParser(jsonObjectMapper, eventProcessor)

            val processExecutor: ProcessExecutor = ProcessExecutor()
                    .command(commandLine)
                    .readOutput(true)
                    .addListener(object : ProcessListener() {
                        override fun afterStart(process: Process, executor: ProcessExecutor) {
                            testExecutionsById[executionId] = execution.toRunning(
                                    stopper = ProcessKillerTestExecutionStopper(executionId, process, eventProcessor)
                            )
                        }

                        override fun afterStop(process: Process?) {
                            testExecutionsById.remove(executionId)
                        }
                    })
                    .redirectOutput(
                            object : LogOutputStream() {
                                override fun processLine(line: String) {
                                    testRunnerEventProcessor.processEvent(line)
                                }
                            }
                    )

            val startTime = System.currentTimeMillis()
            val processResult: ProcessResult = processExecutor.execute()
            LOGGER.debug("execution took ${System.currentTimeMillis() - startTime} ms")

            if (processResult.exitValue == ExitCode.RUNNER_FAILED.code) {
                handleError(
                        eventProcessor,
                        errorMessage = """|process exited with code ${processResult.exitValue}
                                          |commandLine = $commandLine
                                          |args = $args
                                          |output = [${processResult.outputUTF8()}]
                                       """.trimMargin()
                )
            }
        } catch (e: Exception) {
            handleError(
                    eventProcessor,
                    errorMessage = """|failed to start the runner process
                                      |commandLine = $commandLine
                                      |args = $args
                                      |exception = ${e.toStringWithStacktrace()}
                                   """.trimMargin()
            )
        } finally {
            try {
                Files.delete(argsFile)
            } catch (e: Exception) {
                println("failed to delete argsFile [$argsFile]")
            }
            LOGGER.debug("==========================================[ DONE ]=========================================")
            doneProcessor()
        }
    }

    private fun createCommandLine(argsFile: java.nio.file.Path): List<String> {
        val commandLine = mutableListOf<String>()

        commandLine += getJavaBinaryPath().toString()

        commandLine += "-classpath"
        commandLine += "${getRunnerRepoPath()}/*"

        commandLine += "-Dtesterum.packageDirectory=${getPackageDir()}"
        commandLine += "-XX:-OmitStackTraceInFastThrow"
//        commandLine += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000" // todo: make this an option to allow people to do remote debugging

        commandLine += "com.testerum.runner_cmdline.TesterumRunner"
        commandLine += "@$argsFile"

        LOGGER.debug("commandLine = {}", commandLine)

        return commandLine
    }

    private fun getJavaBinaryPath(): java.nio.file.Path {
        val javaHome = Paths.get(
                System.getProperty("java.home")
        ).toAbsolutePath().normalize()

        return if (IS_WINDOWS) {
            javaHome.resolve("bin/java.exe")
        } else {
            javaHome.resolve("bin/java")
        }
    }

    private fun getRunnerRepoPath(): java.nio.file.Path {
        val packageDir: java.nio.file.Path = getPackageDir()

        return packageDir.resolve("runner/repo")
                         .toAbsolutePath()
                         .normalize()
    }

    private fun getPackageDir(): java.nio.file.Path {
        val packageDirKey = SystemSettingKeys.TESTERUM_INSTALL_DIR
        val packageDirSettingValue = settingsManager.getValue(packageDirKey)!!

        return Paths.get(packageDirSettingValue)
                    .toAbsolutePath()
                    .normalize()
    }

    private fun getBuiltInBasicStepsDirectory(): java.nio.file.Path {
        val builtInBasicStepsDir = settingsManager.getRequiredSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR)
                .resolvedValueAsPath

        return builtInBasicStepsDir.toAbsolutePath().normalize()
    }

    private fun getRepositoryDirectory(): java.nio.file.Path {
        val repoDir = settingsManager.getRequiredSetting(SystemSettingKeys.REPOSITORY_DIR)
                .resolvedValueAsPath

        return repoDir.toAbsolutePath().normalize()
    }

    private fun createArgsFile(args: List<String>): java.nio.file.Path {
        val argsFile: java.nio.file.Path = Files.createTempFile("testerum-runner-", ".cmdline-options").toAbsolutePath().normalize()
        argsFile.toFile().deleteOnExit()

        Files.write(argsFile, args)

        return argsFile
    }

    private fun createArgs(testsPathsToRun: List<Path>): List<String> {
        val args = mutableListOf<String>()

        // repository directory
        val repositoryDir: java.nio.file.Path = getRepositoryDirectory()
        args += "--repository-directory '${repositoryDir.escape()}'"

        // built-in basic steps
        val builtInBasicStepsDir: java.nio.file.Path = getBuiltInBasicStepsDirectory()
        args += "--basic-steps-directory '${builtInBasicStepsDir.escape()}'"

        // output
        args += "--output-format JSON"

        // tests
        for (testPathToRun in testsPathsToRun) {
            val path: java.nio.file.Path = repositoryDir.resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
                                                        .resolve(testPathToRun.toString())
                                                        .toAbsolutePath()
                                                        .normalize()

            args.add("--test-path '${path.escape()}'")
        }

        // settings
        val keysOfSettingsToExclude = setOf(
                SystemSettingKeys.TESTERUM_INSTALL_DIR,
                SystemSettingKeys.REPOSITORY_DIR,
                SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR
        )
        val settings: Map<String, String?> = settingsManager.getSettings()
                .associateBy({ it.definition.key }, { it.resolvedValue })
        for (setting in settings) {
            if (setting.key in keysOfSettingsToExclude) {
                continue
            }
            if (setting.value == null) {
                continue
            }

            args.add("--setting '${setting.key.escape()}=${setting.value.escape()}'")
        }

        LOGGER.debug("args = {}", args)

        return args
    }

    private fun handleError(eventProcessor: (event: RunnerEvent) -> Unit, errorMessage: String) {
        LOGGER.error(errorMessage)
        eventProcessor(
                RunnerErrorEvent(
                        time = LocalDateTime.now(),
                        errorMessage = errorMessage
                )
        )
    }

    private fun Any?.escape(): String? = this?.toString()
                                             ?.replace("\\", "\\\\")
                                             ?.replace("\"", "\\\"")
                                             ?.replace("'", "\\'")

}
