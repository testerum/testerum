package com.testerum.runner_cmdline

import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.common_kotlin.runWithThreadContextClassLoader
import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.file_service.file.VariablesFileService
import com.testerum.runner.events.model.ConfigurationEvent
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner.glue_object_factory.GlueObjectFactory
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
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerSettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerTesterumDirs
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import com.testerum_api.testerum_steps_api.transformer.Transformer
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class RunnerApplication(private val runnerProjectManager: RunnerProjectManager,
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
            // create execution tree
            val suite = getRunnerSuiteToBeExecuted(cmdlineParams)

            tryToExecute(cmdlineParams, suite)
        } catch (e: Exception) {
            testerumLogger.error("execution failure", e)

            ExitCode.RUNNER_FAILED
        }
    }

    fun getRunnerSuiteToBeExecuted(cmdlineParams: CmdlineParams): RunnerSuite {
        initialize(cmdlineParams)

        val testsDir = runnerProjectManager.getProjectServices().dirs().getTestsDir()
        val suite: RunnerSuite = runnerExecutionTreeBuilder.createTree(cmdlineParams, testsDir)

        return suite;
    }

    fun jUnitExecute(cmdlineParams: CmdlineParams, suite: RunnerSuite): ExitCode {
        return tryToExecute(cmdlineParams, suite)
    }

    private fun tryToExecute(cmdlineParams: CmdlineParams, suite: RunnerSuite): ExitCode {

        logRunnerSuite(suite)

        // setup runner services
        val stepsClassLoader: ClassLoader = Thread.currentThread().contextClassLoader
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

        triggerConfigurationEvent(cmdlineParams)

        basicStepsCache.initialize()
    }

    private fun triggerConfigurationEvent(cmdlineParams: CmdlineParams) {
        val projectInfo = TesterumProjectFileService().load(cmdlineParams.repositoryDirectory)

        eventsService.logEvent(
            ConfigurationEvent(
                time = LocalDateTime.now(),
                eventKey = EventKey.LOG_EVENT_KEY,
                projectId = projectInfo.id,
                projectName = projectInfo.name,
                verbose = cmdlineParams.verbose,
                repositoryDirectory = cmdlineParams.repositoryDirectory.toString(),
                variablesEnvironment = cmdlineParams.variablesEnvironment,
                variableOverrides = cmdlineParams.variableOverrides,
                settingsFile = cmdlineParams.settingsFile.toString(),
                settingOverrides = cmdlineParams.settingOverrides,
                testPaths = cmdlineParams.testPaths,
                tagsToInclude = cmdlineParams.tagsToInclude,
                tagsToExclude = cmdlineParams.tagsToExclude,
                reportsWithProperties = cmdlineParams.reportsWithProperties,
                managedReportsDir = cmdlineParams.managedReportsDir.toString(),
                executionName = cmdlineParams.executionName
            )
        )
    }

    private fun getProjectVariablesDir(): JavaPath = runnerProjectManager.getProjectServices().dirs().getVariablesDir()

    private fun logRunnerSuite(suite: RunnerSuite) {
        println("------------------------------[ execution tree ]------------------------------")
        print(suite.toString())
        println("------------------------------------------------------------------------------\n")
    }
}
