package net.qutester.service.tests_runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.settings.SystemSettings
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.repository.enums.FileType
import net.qutester.service.tests_runner.event_bus.TestRunnerEventBus
import net.qutester.service.tests_runner.event_processor.TestRunnerEventProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.stream.LogOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class TestsRunnerService(private val settingsManager: SettingsManager,
                         private val jsonObjectMapper: ObjectMapper,
                         private val testRunnerEventBus: TestRunnerEventBus) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TestsRunnerService::class.java)

        private val OS_NAME: String = System.getProperty("os.name").toLowerCase()
        private val IS_WINDOWS: Boolean = OS_NAME.startsWith("win")
    }

    fun executeTests(testsPathsToRun: List<Path>) {
        val packageDirKey = SystemSettings.TESTERUM_INSTALL_DIRECTORY.key
        val packageDir: java.nio.file.Path = Paths.get(
                settingsManager.getSettingValue(packageDirKey)!!
        ).toAbsolutePath().normalize()

        val repositoryDir: java.nio.file.Path = Paths.get(
                settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY)
        ).toAbsolutePath().normalize()

        val basicStepsDir: java.nio.file.Path = Paths.get(
                settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)
        ).toAbsolutePath().normalize()

        val shellScriptPath: java.nio.file.Path = getShellScriptPath(packageDir)

        // environment vars
        val environmentVars: Map<String, String> = mapOf(
                "JAVA_OPTS" to "-D$packageDirKey=$packageDir"
        )

        // command-line
        val args: MutableList<String> = mutableListOf(
                "--repository-directory '${repositoryDir.toWindowsFriendly()}'",
                "--basic-steps-directory '${basicStepsDir.toWindowsFriendly()}'",
                "--output-format JSON"
        )

        // command-line: add tests
        for (testPathToRun in testsPathsToRun) {
            // todo: remove this hard-coding of "rests"
            val path: java.nio.file.Path = repositoryDir.resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
                                                        .resolve(testPathToRun.toString())
                                                        .toAbsolutePath()
                                                        .normalize()

            args.add("--test-path '${path.toWindowsFriendly()}'")
        }

        // command-line: add settings
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

        val argsTempFile: java.nio.file.Path = Files.createTempFile("testerum-runnner-", ".cmdline-options").toAbsolutePath().normalize()
        argsTempFile.toFile().deleteOnExit()

        Files.write(argsTempFile, args)

        val commandLine: List<String> = getCommandLine(shellScriptPath, argsTempFile)

        println("commandLine = $commandLine")
        println("args = $args")

        val testRunnerEventProcessor = TestRunnerEventProcessor(jsonObjectMapper, testRunnerEventBus)

        val startTime = System.currentTimeMillis()
        val processResult: ProcessResult = ProcessExecutor()
                .command(commandLine)
                .environment(environmentVars)
                .readOutput(true)
                .redirectOutput(
                        object : LogOutputStream() {
                            override fun processLine(line: String?) {
                                testRunnerEventProcessor.processEvent(line)
                            }
                        }
                )
                .execute()

        try {
            Files.delete(argsTempFile)
        } catch (e: Exception) {
            println("failed to delete argsTempFile [$argsTempFile]")
        }

        println("==========================================[ DONE ]=========================================")
        val endTime = System.currentTimeMillis()

        println("execution took ${endTime - startTime} ms")

        if (processResult.exitValue != 0) {
            // todo: send to UI also
            LOGGER.error(
                    """
                        process exited with code ${processResult.exitValue}
                        commandList = $commandLine
                        output = [${processResult.outputUTF8()}]
                    """.trimIndent()
            )
        }
    }

    private fun getShellScriptPath(packageDir: java.nio.file.Path): java.nio.file.Path
            = packageDir.resolve("runner/bin")
            .resolve(if (IS_WINDOWS) { "testerum-runner.bat" } else { "testerum-runner.sh" })
            .toAbsolutePath()
            .normalize()

    private fun getCommandLine(shellScriptPath: java.nio.file.Path, argsTempFile: java.nio.file.Path): List<String> {
        val result = mutableListOf<String>()

        if (IS_WINDOWS) {
            result.add("cmd.exe")
            result.add("/C")
        } else {
            result.add("/bin/sh")
            result.add("-c")
        }

        result.add("$shellScriptPath @${argsTempFile.toWindowsFriendly()}")

        return result
    }

    private fun java.nio.file.Path.toWindowsFriendly()
        = this.toString().replace('\\', '/')

}