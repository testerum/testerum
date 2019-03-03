package com.testerum.runner_cmdline

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.TestContext
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.settings.RunnerSettingsManager
import com.testerum.api.test_context.settings.RunnerTesterumDirs
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.Transformer
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.runWithThreadContextClassLoader
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.file_service.file.VariablesFileService
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner_cmdline.classloader.RunnerClassloaderFactory
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.object_factory.GlueObjectFactoryFinder
import com.testerum.runner_cmdline.project_manager.RunnerProjectManager
import com.testerum.runner_cmdline.runner_tree.builder.RunnerExecutionTreeBuilder
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner_cmdline.test_context.TestContextImpl
import com.testerum.runner_cmdline.transformer.TransformerFactory
import com.testerum.settings.TesterumDirs
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class RunnerApplication(private val runnerProjectManager: RunnerProjectManager,
                        private val runnerClassloaderFactory: RunnerClassloaderFactory,
                        private val runnerSettingsManager: RunnerSettingsManager,
                        private val runnerTesterumDirs: RunnerTesterumDirs,
                        private val testerumDirs: TesterumDirs,
                        private val eventsService: EventsService,
                        private val basicStepsCache: BasicStepsCache,
                        private val runnerExecutionTreeBuilder: RunnerExecutionTreeBuilder,
                        private val variablesFileService: VariablesFileService,
                        private val testVariables: TestVariablesImpl,
                        private val executionListenerFinder: ExecutionListenerFinder,
                        private val globalTransformers: List<Transformer<*>>,
                        private val testerumLogger: TesterumLogger,
                        private val stopWatch: StopWatch) {

    // todo: when resolving settings (in the service module), throw exception if a cycle is found

    fun execute(cmdlineParams: CmdlineParams): ExitCode {
        return try {
            tryToExecute(cmdlineParams)
        } catch (e: Exception) {
            System.err.println("execution failure")
            e.printStackTrace(System.err)

            ExitCode.RUNNER_FAILED
        }
    }

    private fun tryToExecute(cmdlineParams: CmdlineParams): ExitCode {
        initialize(cmdlineParams)

        // create execution tree
        val testsDir = runnerProjectManager.getProjectServices().dirs().getTestsDir()
        val suite: RunnerSuite = runnerExecutionTreeBuilder.createTree(cmdlineParams, testsDir)
        logRunnerSuite(suite)


        // setup runner services
        val stepsClassLoader: ClassLoader = runnerClassloaderFactory.createStepsClassLoader()
        val testContext = TestContextImpl(stepsClassLoader = stepsClassLoader)
        @Suppress("DEPRECATION")
        run {
            TesterumServiceLocator.registerService(TestVariables::class.java, testVariables)
            TesterumServiceLocator.registerService(RunnerSettingsManager::class.java, runnerSettingsManager)
            TesterumServiceLocator.registerService(RunnerTesterumDirs::class.java, runnerTesterumDirs)
            TesterumServiceLocator.registerService(TesterumLogger::class.java, testerumLogger)
            TesterumServiceLocator.registerService(TestContext::class.java, testContext)
        }
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

        // setup variables
        val projectId = runnerProjectManager.getProjectServices().project.id
        val globalVars = GlobalVariablesContext.from(
                variablesFileService.getMergedVariables(
                        projectVariablesDir = getProjectVariablesDir(),
                        fileLocalVariablesFile = testerumDirs.getFileLocalVariablesFile(),
                        projectId = projectId,
                        currentEnvironment = cmdlineParams.variablesEnvironment,
                        variableOverrides = cmdlineParams.variableOverrides
                )
        )

        // execute tests
        eventsService.start()
        println("STARTUP TIME: ${stopWatch.elapsedMillis()}ms")
        val executionStatus: ExecutionStatus = runWithThreadContextClassLoader(stepsClassLoader) {
            suite.run(runnerContext, globalVars)
        }

        // report status
        return if (executionStatus == ExecutionStatus.PASSED) {
            ExitCode.OK
        } else {
            ExitCode.TEST_SUITE_FAILED
        }
    }

    private fun initialize(cmdlineParams: CmdlineParams) {
        executionListenerFinder.setReports(cmdlineParams.reportsWithProperties, cmdlineParams.managedReportsDir)

        val basicStepsDir = testerumDirs.getBasicStepsDir()

        basicStepsCache.initialize(
                stepLibraryJarFiles = getStepLibraryJarFiles(basicStepsDir),
                persistentCacheFile = getPersistentCacheFile()
        )
    }

    private fun getPersistentCacheFile(): JavaPath {
        val userHomeDir = Paths.get(
                System.getProperty("user.home")
                        ?: throw IllegalStateException("[user.home] system property was not set")
        )

        return userHomeDir.resolve(".testerum/cache/basic-steps-cache.json")
    }

    private fun getProjectVariablesDir(): JavaPath = runnerProjectManager.getProjectServices().dirs().getVariablesDir()

    private fun getStepLibraryJarFiles(basicStepsDir: JavaPath): List<JavaPath> {
        return basicStepsDir.walkAndCollect {
            it.isRegularFile && it.hasExtension(".jar")
        }
    }

    private fun logRunnerSuite(suite: RunnerSuite) {
        println("------------------------------[ execution tree ]------------------------------")
        print(suite.toString())
        println("------------------------------------------------------------------------------\n")
    }

}
