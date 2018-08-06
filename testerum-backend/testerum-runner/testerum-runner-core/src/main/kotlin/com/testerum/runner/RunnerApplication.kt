package com.testerum.runner

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.transformer.Transformer
import com.testerum.common_kotlin.runWithThreadContextClassLoader
import com.testerum.model.repository.enums.FileType
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.runner.BannerPrinter.printBanner
import com.testerum.runner.cmdline.exiter.model.ExitCode
import com.testerum.runner.cmdline.params.model.CmdlineParams
import com.testerum.runner.events.EventsService
import com.testerum.runner.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner.object_factory.GlueObjectFactoryFinder
import com.testerum.runner.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner.runner_tree.nodes.step.RunnerStep
import com.testerum.runner.runner_tree.nodes.step.impl.RunnerBasicStep
import com.testerum.runner.runner_tree.nodes.step.impl.RunnerComposedStep
import com.testerum.runner.runner_tree.nodes.step.impl.RunnerUndefinedStep
import com.testerum.runner.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner.runner_tree.nodes.test.RunnerTest
import com.testerum.runner.runner_tree.runner_context.RunnerContext
import com.testerum.runner.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner.test_context.TestContextImpl
import com.testerum.runner.transformer.TransformerFactory
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import com.testerum.service.hooks.HooksService
import com.testerum.service.step.StepService
import com.testerum.service.tests.TestsService
import com.testerum.service.variables.VariablesService
import com.testerum.settings.SystemSettings
import com.testerum.settings.private_api.SettingsManagerImpl
import org.fusesource.jansi.AnsiConsole
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Collectors.toList

class RunnerApplication(private val settingsManager: SettingsManagerImpl,
                        private val eventsService: EventsService,
                        private val stepService: StepService,
                        private val testsService: TestsService,
                        private val hooksService: HooksService,
                        private val variablesService: VariablesService,
                        private val testVariables: TestVariablesImpl,
                        private val executionListenerFinder: ExecutionListenerFinder,
                        private val globalTransformers: List<Transformer<*>>,
                        private val testerumLogger: TesterumLogger) {

    // todo: when resolving settings (in the service module), throw exception if a cycle is found

    fun execute(cmdlineParams: CmdlineParams): ExitCode {
        return try {
            tryToExecute(cmdlineParams)
        } catch (e: Exception) {
            System.err.println("execution failure")
            e.printStackTrace(System.err)

            ExitCode.EXECUTION_FAILURE
        }
    }

    private fun tryToExecute(cmdlineParams: CmdlineParams): ExitCode {
        AnsiConsole.systemInstall()
        printBanner()

        // todo: print these (with good formatting) when in verbose mode
         println("cmdlineParams = $cmdlineParams")

        executionListenerFinder.outputFormat = cmdlineParams.outputFormat

        setSettings(cmdlineParams)
        stepService.loadSteps()

        println("Settings:")
        println("---------")
        for ((key, _) in settingsManager.settings) {
            println("[$key] = [${settingsManager.getSettingValueOrDefault(key)}]")
        }
        println()

        val stepsClassLoader: ClassLoader = createStepsClassLoader()

        val pathsToTestsToExecute: List<Path> = findPathsToTestsToExecute(cmdlineParams)

        println("Will execute the following tests:")
        for (testPath in pathsToTestsToExecute) {
            println("    ${testPath.toAbsolutePath().normalize()}")
        }

        // create TestContext
        val testContext = TestContextImpl(
                testVariablesImpl = testVariables,
                settingsManager = settingsManager,
                stepsClassLoader = stepsClassLoader,
                logger = testerumLogger
        )

        // get hooks
        val hooks: List<HookDef> = hooksService.getHooks()

        // load tests
        val suite: RunnerSuite = loadTests(pathsToTestsToExecute, hooks, cmdlineParams)

        val glueObjectFactory: GlueObjectFactory = GlueObjectFactoryFinder.findGlueObjectFactory(testContext)

        val transformerFactory = TransformerFactory(glueObjectFactory, globalTransformers)

        // create RunnerContext
        val runnerContext = RunnerContext(
                eventsService = eventsService,
                stepsClassLoader = stepsClassLoader,
                glueObjectFactory = glueObjectFactory,
                transformerFactory = transformerFactory,
                testVariables = testVariables,
                testContext = testContext
        )

        // add steps to GlueObjectFactory
        suite.addClassesToGlueObjectFactory(runnerContext)

        // execute tests
        val globalVars = GlobalVariablesContext.from(
                variablesService.getVariablesAsMap()
        )

        val executionStatus: ExecutionStatus = runWithThreadContextClassLoader(stepsClassLoader) {
            suite.run(runnerContext, globalVars)
        }

        return if (executionStatus == ExecutionStatus.PASSED) {
            ExitCode.OK
        } else {
            ExitCode.EXECUTION_FAILURE
        }
    }


    private fun setSettings(cmdlineParams: CmdlineParams) {
        val settings = mutableMapOf<String, String>()

        settings[SystemSettings.REPOSITORY_DIRECTORY.key] = cmdlineParams.repositoryDirectory.toAbsolutePath().normalize().toString()
        settings[SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY.key] = cmdlineParams.basicStepsDirectory.toAbsolutePath().normalize().toString()
        // todo: user step directory
        settings.putAll(cmdlineParams.settingOverrides)

        settingsManager.registerPossibleUnresolvedValues(settings)
    }

    // todo: how to reduce duplication between this class and StepLibraryCacheManger?
    private fun createStepsClassLoader(): ClassLoader {
        val jars: List<Path> = getStepJarFiles()

        val jarUrls: Array<URL> = jars
                .filter { !it.toString().contains("testerum-api-") } // todo: remove this hack - run the scanner in a separate process and scan the classpath instead
                .map { it.toUri().toURL() }
                .toTypedArray()

        return URLClassLoader(jarUrls, RunnerApplication::class.java.classLoader)
    }

    // todo: how to reduce duplication between this class and BasicStepScanner?
    private fun getStepJarFiles(): List<Path> {
        // todo: user step directory
        val basicStepsDirectory: Path = Paths.get(
                settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)
        )

        val isJarFile = BiPredicate { file: Path, _: BasicFileAttributes ->
            Files.isRegularFile(file) && file.toString().endsWith(".jar")
        }

        Files.find(basicStepsDirectory, 1, isJarFile).use { stream ->
            return stream.collect(toList())
        }
    }

    private fun loadTests(pathsToTestsToExecute: List<Path>, hooks: List<HookDef>, cmdlineParams: CmdlineParams): RunnerSuite {
        val tests = mutableListOf<RunnerTest>()

        val beforeEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_EACH_TEST)
        val afterEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_EACH_TEST)

        for ((index, testPath) in pathsToTestsToExecute.withIndex()) {
            val test: TestModel = loadTest(testPath, cmdlineParams)

            val runnerTest: RunnerTest = createRunnerTest(test, testPath, index, beforeEachTestHooks, afterEachTestHooks)

            tests += runnerTest
        }

        val beforeAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
        val afterAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_ALL_TESTS)

        return RunnerSuite(beforeAllTestsHooks, tests, afterAllTestsHooks)
    }

    private fun List<HookDef>.sortedHooksForPhase(phase: HookPhase): List<RunnerHook>
            = this.asSequence()
                  .filter { it.phase == phase }
                  .sortedBy { it.order }
                  .map { RunnerHook(it) }
                  .toList()

    private fun createRunnerTest(test: TestModel,
                                 filePath: Path,
                                 testIndexInParent: Int,
                                 beforeEachTestHooks: List<RunnerHook>,
                                 afterEachTestHooks: List<RunnerHook>): RunnerTest {
        val runnerSteps = mutableListOf<RunnerStep>()

        for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
            runnerSteps += createRunnerStep(stepCall, stepIndexInParent)
        }

        return RunnerTest(
                test = test,
                filePath = filePath,
                indexInParent = testIndexInParent,
                steps = runnerSteps,
                beforeEachTestHooks = beforeEachTestHooks,
                afterEachTestHooks = afterEachTestHooks
        )
    }

    private fun createRunnerStep(stepCall: StepCall, indexInParent: Int): RunnerStep {
        val stepDef = stepCall.stepDef

        return when (stepDef) {
            is UndefinedStepDef -> RunnerUndefinedStep(stepCall, indexInParent)
            is BasicStepDef -> RunnerBasicStep(stepCall, indexInParent)
            is ComposedStepDef -> {
                val nestedSteps = mutableListOf<RunnerStep>()

                for ((nestedIndexInParent, nestedStepCall) in stepDef.stepCalls.withIndex()) {
                    val nestedRunnerStep = createRunnerStep(nestedStepCall, nestedIndexInParent)

                    nestedSteps += nestedRunnerStep
                }

                RunnerComposedStep(
                        stepCall = stepCall,
                        indexInParent = indexInParent,
                        steps = nestedSteps
                )
            }
            else -> throw RuntimeException("unknown StepDef type [${stepDef.javaClass.name}]")
        }
    }

    private fun loadTest(testPath: Path,
                         cmdlineParams: CmdlineParams): TestModel {
        try {
            val relativeTestPath = cmdlineParams
                    .repositoryDirectory
                    .resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
                    .toAbsolutePath()
                    .relativize(testPath)

            val testerumPath = com.testerum.model.infrastructure.path.Path.createInstance(relativeTestPath.toString())

            return testsService.getTestAtPath(testerumPath)
                    ?: throw RuntimeException("could not find test at [${testPath.toAbsolutePath().normalize()}]")
        } catch (e: Exception) {
            throw RuntimeException("failed to load test at [${testPath.toAbsolutePath().normalize()}]", e)
        }
    }

    private fun findPathsToTestsToExecute(cmdlineParams: CmdlineParams): List<Path> {
        if (cmdlineParams.testFilesOrDirectories.isEmpty()) {
            return findTestsUnderDirectory(
                    cmdlineParams.repositoryDirectory.resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
            )
        } else {
            val result = mutableListOf<Path>()

            for (testFilesOrDirectory in cmdlineParams.testFilesOrDirectories) {
                if (Files.isDirectory(testFilesOrDirectory)) {
                    result.addAll(
                            findTestsUnderDirectory(testFilesOrDirectory)
                    )
                } else {
                    if (Files.isRegularFile(testFilesOrDirectory) && testFilesOrDirectory.fileName.toString().endsWith(".test")) {
                        result.add(testFilesOrDirectory.toAbsolutePath().normalize())
                    }
                }
            }

            return result
        }
    }

    private fun findTestsUnderDirectory(path: Path): List<Path> {
        val result = mutableListOf<Path>()

        Files.walk(path).use { pathStream ->
            pathStream.forEach { path ->
                if (Files.isRegularFile(path) && path.fileName.toString().endsWith(".test")) {
                    result.add(path.toAbsolutePath().normalize())
                }
            }
        }

        result.sortBy { it.toAbsolutePath().normalize().toString() }

        return result
    }

}

