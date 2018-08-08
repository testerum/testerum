package com.testerum.service.tests_runner.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.service.tests_runner.execution.stopper.ProcessKillerTestExecutionStopper
import com.testerum.service.tests_runner.execution.stopper.TestExecutionStopper
import com.testerum.settings.SystemSettings
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

class TestsExecutionService(private val settingsManager: SettingsManager,
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
    private val testExecutions: MutableMap<Long, TestExecutionStopper> = ConcurrentHashMap()

    fun createExecution(): Long = testExecutionIdGenerator.nextId()

    fun stopExecution(executionId: Long) {
        val execution = testExecutions[executionId]
        if (execution == null) {
            LOGGER.warn("trying to stop an execution that no longer exists")
            return
        }

        execution.stopExecution()
        testExecutions.remove(executionId)
    }

    fun startExecution(executionId: Long,
                       testsPathsToRun: List<Path>,
                       eventProcessor: (event: RunnerEvent) -> Unit) {
        LOGGER.debug("==========================================[ start test execution {} ]=========================================", executionId)

        val args = createArgs(testsPathsToRun)
        val argsFile: java.nio.file.Path = createArgsFile(args)

        try {
            val commandLine: List<String> = createCommandLine(argsFile)

            val testRunnerEventProcessor = TestRunnerEventParser(jsonObjectMapper, eventProcessor)

            val processExecutor: ProcessExecutor = ProcessExecutor()
                    .command(commandLine)
                    .readOutput(true)
                    .addListener(object : ProcessListener() {
                        override fun afterStart(process: Process, executor: ProcessExecutor) {
                            testExecutions[executionId] = ProcessKillerTestExecutionStopper(executionId, process)
                        }

                        override fun afterStop(process: Process?) {
                            testExecutions.remove(executionId)
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

            if (processResult.exitValue != 0) {
                val errorMessage =
                        """|process exited with code ${processResult.exitValue}
                           |args = $args
                           |commandLine = $commandLine
                           |output = [${processResult.outputUTF8()}]
                        """.trimMargin()

                LOGGER.error(errorMessage)
                eventProcessor(
                        TextLogEvent(
                                time = LocalDateTime.now(),
                                eventKey = EventKey.SUITE_EVENT_KEY,
                                logLevel = LogLevel.WARNING,
                                message = errorMessage
                        )
                )
            }
        } finally {
            try {
                Files.delete(argsFile)
            } catch (e: Exception) {
                println("failed to delete argsFile [$argsFile]")
            }
            LOGGER.debug("==========================================[ DONE ]=========================================")
        }
    }

    private fun createCommandLine(argsFile: java.nio.file.Path): List<String> {
        val commandLine = mutableListOf<String>()

        commandLine += getJavaBinaryPath().toWindowsFriendly()

        commandLine += "-classpath"
        commandLine += "${getRunnerRepoPath()}/*"

        commandLine += "-Dtesterum.packageDirectory=${getPackageDir().toWindowsFriendly()}"
        commandLine += "-XX:-OmitStackTraceInFastThrow"
//        commandLine += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000" // todo: make this an option to allow people to do remote debugging

        commandLine += "com.testerum.runner_cmdline.TesterumRunner"
        commandLine += "@${argsFile.toWindowsFriendly()}"

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

    // todo: move these to SettingsManager
    private fun getPackageDir(): java.nio.file.Path {
        val packageDirKey = SystemSettings.TESTERUM_INSTALL_DIRECTORY.key
        val packageDirSettingValue = settingsManager.getSettingValue(packageDirKey)!!

        return Paths.get(packageDirSettingValue)
                    .toAbsolutePath()
                    .normalize()
    }

    private fun getBuiltInBasicStepsDirectory(): java.nio.file.Path {
        val builtInBasicStepsDirSettingValue = settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)

        return Paths.get(builtInBasicStepsDirSettingValue)
                .toAbsolutePath()
                .normalize()
    }

    private fun getRepositoryDirectory(): java.nio.file.Path {
        val repoDirSettingValue = settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY)

        return Paths.get(repoDirSettingValue)
                .toAbsolutePath()
                .normalize()
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
        args += "--repository-directory '${repositoryDir.toWindowsFriendly()}'"

        // built-in basic steps
        val builtInBasicStepsDir: java.nio.file.Path = getBuiltInBasicStepsDirectory()
        args += "--basic-steps-directory '${builtInBasicStepsDir.toWindowsFriendly()}'"

        // output
        args += "--output-format JSON"

        // tests
        for (testPathToRun in testsPathsToRun) {
            val path: java.nio.file.Path = repositoryDir.resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
                                                        .resolve(testPathToRun.toString())
                                                        .toAbsolutePath()
                                                        .normalize()

            args.add("--test-path '${path.toWindowsFriendly()}'")
        }

        // settings
        val keysOfSettingsToExclude = setOf(
                SystemSettings.TESTERUM_INSTALL_DIRECTORY.key,
                SystemSettings.REPOSITORY_DIRECTORY.key,
                SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY.key
        )
        val settings: Map<String, String?> = settingsManager.getAllSettings()
                .associateBy({ it.setting.key }, { it.value })
        for (setting in settings) {
            if (setting.key in keysOfSettingsToExclude) {
                continue
            }
            if (setting.value == null) {
                continue
            }

            args.add("--setting '${setting.key}=${setting.value}'")
        }

        LOGGER.debug("args = {}", args)

        return args
    }

    private fun java.nio.file.Path.toWindowsFriendly()= this.toString().replace('\\', '/')

}
