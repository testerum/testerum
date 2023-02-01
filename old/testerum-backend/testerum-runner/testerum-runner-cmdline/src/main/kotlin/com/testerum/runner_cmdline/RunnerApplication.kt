package com.testerum.runner_cmdline

import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.list
import com.testerum.common_kotlin.runWithThreadContextClassLoader
import com.testerum.common_kotlin.toUrlArray
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.runner.tree.id.RunnerIdCreator
import com.testerum.runner.events.model.ConfigurationEvent
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.object_factory.GlueObjectFactoryFinder
import com.testerum.runner_cmdline.project_manager.RunnerProjectManager
import com.testerum.runner_cmdline.runner_api_services.ScriptExecuterImpl
import com.testerum.runner_cmdline.runner_api_services.TestVariablesImpl
import com.testerum.runner_cmdline.runner_tree.RunnerExecutionTreeBuilder
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum.runner_cmdline.test_context.TestContextImpl
import com.testerum.runner_cmdline.transformer.TransformerFactory
import com.testerum.settings.TesterumDirs
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.script_executer.ScriptExecuter
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerSettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerTesterumDirs
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import com.testerum_api.testerum_steps_api.transformer.Transformer
import java.net.URLClassLoader
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class RunnerApplication(
    private val runnerProjectManager: RunnerProjectManager,
    private val runnerSettingsManager: RunnerSettingsManager,
    private val runnerTesterumDirs: RunnerTesterumDirs,
    private val testerumDirs: TesterumDirs,
    private val eventsService: EventsService,
    private val runnerExecutionTreeBuilder: RunnerExecutionTreeBuilder,
    private val variablesFileService: VariablesFileService,
    private val executionListenerFinder: ExecutionListenerFinder,
    private val globalTransformers: List<Transformer<*>>,
    private val testerumLogger: TesterumLogger,
    private val stopWatch: StopWatch
) {

    // todo: when resolving settings (in the service module), throw exception if a cycle is found

    fun execute(cmdlineParams: RunCmdlineParams): ExitCode {
        return try {
            // create execution tree
            val suite = getRunnerSuiteToBeExecuted(cmdlineParams)

            tryToExecute(cmdlineParams, suite)
        } catch (e: Exception) {
            testerumLogger.error("execution failure", e)

            ExitCode.RUNNER_FAILED
        }
    }

    fun getRunnerSuiteToBeExecuted(cmdlineParams: RunCmdlineParams): RunnerSuite {
        initialize(cmdlineParams)

        val testsDir = runnerProjectManager.getProjectServices().dirs().getTestsDir()
        val suite: RunnerSuite = runnerExecutionTreeBuilder.createTree(cmdlineParams, testsDir)

        return suite
    }

    fun jUnitExecute(cmdlineParams: RunCmdlineParams, suite: RunnerSuite): ExitCode {
        return tryToExecute(cmdlineParams, suite)
    }

    private fun tryToExecute(cmdlineParams: RunCmdlineParams, suite: RunnerSuite): ExitCode {

        logRunnerSuite(suite)

        // setup runner services
        val stepsClassLoader: ClassLoader = stepsClassLoader()
        val testContext = TestContextImpl(stepsClassLoader = stepsClassLoader)
        val glueObjectFactory: GlueObjectFactory = GlueObjectFactoryFinder.findGlueObjectFactory(testContext)
        val transformerFactory = TransformerFactory(glueObjectFactory, globalTransformers)

        // setup variables
        val projectId = runnerProjectManager.getProjectServices().project.id
        val variablesContext = VariablesContext(
            globalVarsMap = variablesFileService.getMergedVariables(
                projectVariablesDir = getProjectVariablesDir(),
                fileLocalVariablesFile = testerumDirs.getFileLocalVariablesFile(),
                projectId = projectId,
                currentEnvironment = cmdlineParams.variablesEnvironment,
                variableOverrides = cmdlineParams.variableOverrides
            )
        )
        val testVariables = TestVariablesImpl(variablesContext)
        val runnerContext = RunnerContext(
            eventsService = eventsService,
            stepsClassLoader = stepsClassLoader,
            glueObjectFactory = glueObjectFactory,
            transformerFactory = transformerFactory,
            variablesContext = variablesContext,
            testVariables = testVariables,
            testContext = testContext
        )
        val scriptExecuter = ScriptExecuterImpl(variablesContext)

        // register runner services
        @Suppress("DEPRECATION")
        run {
            TesterumServiceLocator.registerService(TestVariables::class.java, testVariables)
            TesterumServiceLocator.registerService(RunnerSettingsManager::class.java, runnerSettingsManager)
            TesterumServiceLocator.registerService(RunnerTesterumDirs::class.java, runnerTesterumDirs)
            TesterumServiceLocator.registerService(TesterumLogger::class.java, testerumLogger)
            TesterumServiceLocator.registerService(TestContext::class.java, testContext)
            TesterumServiceLocator.registerService(ScriptExecuter::class.java, scriptExecuter)
        }

        // add steps to GlueObjectFactory
        for (glueClassName in suite.glueClassNames) {
            val glueClass: Class<*> = try {
                runnerContext.stepsClassLoader.loadClass(glueClassName)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException("failed to load glue class [$glueClassName]", e)
            }

            runnerContext.glueObjectFactory.addClass(glueClass)
        }

        // execute tests
        eventsService.start()
        println("STARTUP TIME: ${stopWatch.elapsedMillis()}ms")
        val executionStatus: ExecutionStatus = runWithThreadContextClassLoader(stepsClassLoader) {
            suite.execute(runnerContext)
        }

        // report status
        return if (executionStatus == ExecutionStatus.PASSED) {
            ExitCode.OK
        } else {
            ExitCode.TEST_SUITE_FAILED
        }
    }

    private fun stepsClassLoader(): ClassLoader {
        val additionalBasicStepsDir = runnerProjectManager.getProjectServices().dirs().getAdditionalBasicStepsDir()
        val additionalJars = additionalBasicStepsDir.list { it.hasExtension(".jar") }
            .toUrlArray()

        return URLClassLoader(
            additionalJars,
            Thread.currentThread().contextClassLoader
        )
    }

    private fun initialize(cmdlineParams: RunCmdlineParams) {
        val reportsWithProperties = if (cmdlineParams.reportsWithProperties.isEmpty()) {
            listOf("CONSOLE")
        } else {
            cmdlineParams.reportsWithProperties
        }

        executionListenerFinder.setReports(reportsWithProperties, cmdlineParams.managedReportsDir)

        triggerConfigurationEvent(cmdlineParams)
    }

    private fun triggerConfigurationEvent(cmdlineParams: RunCmdlineParams) {
        val projectInfo = TesterumProjectFileService().load(cmdlineParams.repositoryDirectory)

        eventsService.logEvent(
            ConfigurationEvent(
                time = LocalDateTime.now(),
                eventKey = RunnerIdCreator.getRootId(),
                projectId = projectInfo.id,
                projectName = projectInfo.name,
                verbose = cmdlineParams.verbose,
                repositoryDirectory = cmdlineParams.repositoryDirectory.toString(),
                variablesEnvironment = cmdlineParams.variablesEnvironment,
                variableOverrides = cmdlineParams.variableOverrides,
                settingsFile = cmdlineParams.settingsFile.toString(),
                settingOverrides = cmdlineParams.settingOverrides,
                testPaths = cmdlineParams.testPaths,
                tagsToInclude = cmdlineParams.includeTags,
                tagsToExclude = cmdlineParams.excludeTags,
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
