package com.testerum.runner_cmdline

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.TestContext
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.settings.RunnerSettingsManager
import com.testerum.api.test_context.settings.RunnerTesterumDirs
import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.Transformer
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.runWithThreadContextClassLoader
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.file.VariablesFileService
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner_cmdline.classloader.RunnerClassloaderFactory
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.object_factory.GlueObjectFactoryFinder
import com.testerum.runner_cmdline.runner_tree.builder.RunnerExecutionTreeBuilder
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum.runner_cmdline.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner_cmdline.test_context.TestContextImpl
import com.testerum.runner_cmdline.transformer.TransformerFactory
import com.testerum.settings.SettingsManager
import com.testerum.settings.TesterumDirs
import com.testerum.settings.hasValue
import com.testerum.settings.keys.SystemSettingKeys
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class RunnerApplication(private val runnerClassloaderFactory: RunnerClassloaderFactory,
                        private val runnerSettingsManager: RunnerSettingsManager,
                        private val runnerTesterumDirs: RunnerTesterumDirs,
                        private val settingsManager: SettingsManager,
                        private val testerumDirs: TesterumDirs,
                        private val eventsService: EventsService,
                        private val stepsCache: StepsCache,
                        private val testsCache: TestsCache,
                        private val featuresCache: FeaturesCache,
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
        val suite: RunnerSuite = runnerExecutionTreeBuilder.createTree(cmdlineParams, getTestsDir(getRepositoryDir()))
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
        val globalVars = GlobalVariablesContext.from(
                variablesFileService.getVariablesAsMap(
                        getVariablesDir(
                                getRepositoryDir()
                        )
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

        val repositoryDir = getRepositoryDir()
        val basicStepsDir = testerumDirs.getBasicStepsDir()
        val composedStepsDir = getComposedStepsDir(repositoryDir)
        val resourcesDir = getResourcesDir(repositoryDir)
        val testsDir = getTestsDir(repositoryDir)
        val featuresDir = getFeaturesDir(repositoryDir)

        stepsCache.initialize(
                stepLibraryJarFiles = getStepLibraryJarFiles(basicStepsDir),
                persistentCacheFile = getPersistentCacheFile(),
                composedStepsDir = composedStepsDir,
                resourcesDir = resourcesDir
        )

        testsCache.initialize(
                testsDir = testsDir,
                resourcesDir = resourcesDir
        )

        featuresCache.initialize(
                featuresDir = featuresDir
        )
    }

    private fun getPersistentCacheFile(): JavaPath {
        val userHomeDir = Paths.get(
                System.getProperty("user.home")
                        ?: throw IllegalStateException("[user.home] system property was not set")
        )

        return userHomeDir.resolve(".testerum/cache/basic-steps-cache.json")
    }

    private fun getComposedStepsDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("composed_steps")

    private fun getResourcesDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("resources")

    private fun getFeaturesDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("features")

    private fun getTestsDir(repositoryDir: JavaPath): JavaPath = getFeaturesDir(repositoryDir)

    private fun getVariablesDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("variables")

    private fun getRepositoryDir(): JavaPath {
        if (!settingsManager.hasValue(SystemSettingKeys.REPOSITORY_DIR)) {
            throw IllegalStateException("the setting [${SystemSettingKeys.REPOSITORY_DIR}] is required")
        }

        return settingsManager.getSetting(SystemSettingKeys.REPOSITORY_DIR)!!.resolvedValueAsPath
    }

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
