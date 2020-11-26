package com.testerum.web_backend.services.runner.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_jdk.OsUtils
import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.common_kotlin.isDirectory
import com.testerum.file_service.file.LocalVariablesFileService
import com.testerum.model.runner.config.RunConfig
import com.testerum.model.runner.tree.RunnerRootNode
import com.testerum.model.runner.tree.builder.RunnerTreeBuilder
import com.testerum.model.runner.tree.builder.TestPathAndModel
import com.testerum.model.test.TestModel
import com.testerum.model.tests_finder.FeatureTestPath
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath
import com.testerum.model.tests_finder.TestTestPath
import com.testerum.model.tests_finder.TestsFinder
import com.testerum.runner.cmdline.report_type.RunnerReportType
import com.testerum.runner.events.model.RunnerErrorEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.RunnerStoppedEvent
import com.testerum.runner.exit_code.ExitCode
import com.testerum.settings.SettingsManager
import com.testerum.settings.TesterumDirs
import com.testerum.web_backend.filter.project.ProjectDirHolder
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.services.runner.execution.model.RunningTestExecution
import com.testerum.web_backend.services.runner.execution.model.TestExecution
import com.testerum.web_backend.services.runner.execution.model.TestExecutionResponse
import com.testerum.web_backend.services.runner.execution.stopper.ProcessKillerTestExecutionStopper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.nio.file.Path as JavaPath

class TestsExecutionFrontendService(
    private val webProjectManager: WebProjectManager,
    private val testerumDirs: TesterumDirs,
    private val frontendDirs: FrontendDirs,
    private val settingsManager: SettingsManager,
    private val localVariablesFileService: LocalVariablesFileService,
    private val jsonObjectMapper: ObjectMapper
) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TestsExecutionFrontendService::class.java)
    }

    private class TestExecutionIdGenerator {
        private val lastId = AtomicLong(0)

        fun nextId() = lastId.incrementAndGet()
    }

    private val testExecutionIdGenerator = TestExecutionIdGenerator()

    private val testExecutionsById: MutableMap<Long, TestExecution> = ConcurrentHashMap()

    fun createExecution(runConfig: RunConfig): TestExecutionResponse {
        val testsDirectoryRoot = webProjectManager.getProjectServices().dirs().getTestsDir()
        val testPaths = runConfig.pathsToInclude.map {
            val javaPath = testsDirectoryRoot.resolve(it.path.toString())

            when {
                javaPath.isDirectory -> FeatureTestPath(javaPath)
                it.scenarioIndexes.isEmpty() -> TestTestPath(javaPath)
                else -> ScenariosTestPath(javaPath, it.scenarioIndexes)
            }
        }

        val testsMap = TestsFinder.loadTestsToRun(
            testPaths = testPaths,
            tagsToInclude = runConfig.tagsToInclude,
            tagsToExclude = runConfig.tagsToExclude,
            testsDirectoryRoot = testsDirectoryRoot,
            loadTestAtPath = { webProjectManager.getProjectServices().getTestsCache().getTestAtPath(it) }
        )
        val executionId = testExecutionIdGenerator.nextId()
        val projectRootDir = ProjectDirHolder.get().toAbsolutePath().normalize()

        // todo: remove this workaround: the UI should send the environment in the request
        val projectId = webProjectManager.getProjectServices().project.id
        val currentEnvironment = localVariablesFileService.getCurrentEnvironment(
            fileLocalVariablesFile = frontendDirs.getFileLocalVariablesFile(),
            projectId = projectId
        )

        testExecutionsById[executionId] = TestExecution(
            executionId = executionId,
            settings = runConfig.settings,
            testOrDirectoryPathsToRun = runConfig.pathsToInclude,
            projectRootDir = projectRootDir,
            variablesEnvironment = currentEnvironment
        )

        val runnerRootNode = getRunnerRootNode(testsMap)

        return TestExecutionResponse(
            executionId = executionId,
            runnerRootNode = runnerRootNode
        )
    }

    private fun getRunnerRootNode(testsMap: Map<TestPath, TestModel>): RunnerRootNode {
        val builder = RunnerTreeBuilder()

        for ((path, model) in testsMap) {
            builder.addTest(
                TestPathAndModel(path, model)
            )
        }

        return builder.build()
    }

    fun stopExecution(executionId: Long) {
        val execution = testExecutionsById[executionId]
        if (execution == null) {
            LOG.warn("trying to stop an execution that no longer exists")
            return
        }
        if (execution !is RunningTestExecution) {
            LOG.warn("trying to stop an execution that didn't start")
            return
        }

        execution.stopper.stopExecution()
        testExecutionsById.remove(executionId)
    }

    fun startExecution(
        executionId: Long,
        eventProcessor: (event: RunnerEvent) -> Unit,
        doneProcessor: () -> Unit
    ) {
        val execution = testExecutionsById[executionId]
        if (execution == null) {
            LOG.warn("trying to start an execution that no longer exists")
            return
        }
        if (execution is RunningTestExecution) {
            LOG.warn("trying to start an execution that is already started")
            return
        }
        ProjectDirHolder.set(execution.projectRootDir, null)

        LOG.debug("==========================================[ start test execution {} ]=========================================", executionId)

        val args = createArgs(execution)
        val argsFile: JavaPath = createArgsFile(args)
        val commandLine: List<String> = createCommandLine(argsFile)

        try {
            val testRunnerEventProcessor = TestRunnerEventParser(jsonObjectMapper, eventProcessor)

            val processExecutor: ProcessExecutor = ProcessExecutor()
                .command(commandLine)
                .readOutput(true)
                .addListener(object : ProcessListener() {
                    override fun afterStart(process: Process, executor: ProcessExecutor) {
                        testExecutionsById[executionId] = execution.toRunning(
                            stopper = ProcessKillerTestExecutionStopper(executionId, process)
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
            LOG.debug("execution took ${System.currentTimeMillis() - startTime} ms")

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
            LOG.debug("==========================================[ DONE ]=========================================")

            // send RunnerStoppedEvent
            try {
                eventProcessor(
                    RunnerStoppedEvent()
                )
            } catch (e: Exception) {
                LOG.warn("failed to process ${RunnerStoppedEvent::class.simpleName}", e)
            }

            // call doneProcessor
            doneProcessor()
        }
    }

    private fun createCommandLine(argsFile: JavaPath): List<String> {
        val commandLine = mutableListOf<String>()

        commandLine += OsUtils.getJavaBinaryPath().toString()

        commandLine += "-Dfile.encoding=UTF-8"

        commandLine += "-classpath"
        commandLine +=
            "${getRunnerLibPath()}/*" +
                "${File.pathSeparatorChar}" +
                "${testerumDirs.getBasicStepsDir()}/*"

        commandLine += "-Dtesterum.packageDirectory=${testerumDirs.getInstallDir()}"
        commandLine += "-XX:-OmitStackTraceInFastThrow"
//        commandLine += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000" // todo: make this an option to allow people to do remote debugging

        commandLine += "com.testerum.runner_cmdline.TesterumRunner"
        commandLine += "@$argsFile"

        LOG.debug("commandLine = {}", commandLine)

        return commandLine
    }

    private fun getRunnerLibPath(): JavaPath {
        val runnerDir: JavaPath = testerumDirs.getRunnerDir()

        return runnerDir.resolve("lib")
            .toAbsolutePath()
            .normalize()
    }

    private fun createArgsFile(args: List<String>): JavaPath {
        val argsFile: JavaPath = Files.createTempFile("testerum-runner-", ".cmdline-options").toAbsolutePath().normalize()
        argsFile.toFile().deleteOnExit()

        val escapedArgs = args
            .joinToString("\n") {
                it.replace("\\", "\\\\")
                    .replace("\n", "\\\n")
            }

        Files.write(argsFile, escapedArgs.toByteArray(Charsets.UTF_8))

        return argsFile
    }

    private fun createArgs(execution: TestExecution): List<String> {
        val args = mutableListOf<String>()

        val projectDirs = webProjectManager.getProjectServices().dirs()

        // repository directory
        val repositoryDir: JavaPath = projectDirs.projectRootDir

        args += "--repository-directory"
        args += "$repositoryDir"

        // output
        args += "--report"
        args += RunnerReportType.builders().jsonEvents {
            wrapJsonWithPrefixAndPostfix = true
        }

        args += "--managed-reports-directory"
        args += frontendDirs.getReportsDir(webProjectManager.getProjectServices().project.id).toAbsolutePath().normalize().toString()

        // tests
        for (testPathToRun in execution.testOrDirectoryPathsToRun) {
            val path: JavaPath = projectDirs.getTestsDir()
                .resolve(testPathToRun.path.toString())
                .toAbsolutePath()
                .normalize()

            args += "--test-path"
            args += if (testPathToRun.scenarioIndexes.isEmpty()) {
                "$path"
            } else {
                "$path[${testPathToRun.scenarioIndexes.joinToString(separator = ",")}]"
            }
        }

        // variables
        val variablesEnvironment = execution.variablesEnvironment
        if (variablesEnvironment != null) {
            args += "--var-env"
            args += variablesEnvironment
        }

        // settings
        val settings = mutableMapOf<String, String?>()

        // add global settings
        val defaultSettings = settingsManager.getSettings()
            .associateBy({ it.definition.key }, { it.resolvedValue })
        settings.putAll(defaultSettings)

        // override settings from execution, if needed
        for ((key, value) in execution.settings) {
            settings[key] = value
        }

        for (setting in settings) {
            if (setting.value == null) {
                continue
            }

            args.add("--setting")
            args.add("${setting.key}=${setting.value}")
        }

        LOG.debug("args = {}", args)

        return args
    }

    private fun handleError(eventProcessor: (event: RunnerEvent) -> Unit, errorMessage: String) {
        LOG.error(errorMessage)
        eventProcessor(
            RunnerErrorEvent(
                time = LocalDateTime.now(),
                errorMessage = errorMessage
            )
        )
    }

}
