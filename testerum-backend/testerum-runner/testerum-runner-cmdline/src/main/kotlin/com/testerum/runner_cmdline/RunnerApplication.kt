package com.testerum.runner_cmdline

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.TestContext
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.Transformer
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.common_kotlin.runWithThreadContextClassLoader
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
import com.testerum.service.scanner.ScannerService
import com.testerum.service.step.StepService
import com.testerum.service.variables.VariablesService
import com.testerum.settings.private_api.SettingsManagerImpl

class RunnerApplication(private val runnerClassloaderFactory: RunnerClassloaderFactory,
                        private val settingsManager: SettingsManagerImpl,
                        private val eventsService: EventsService,
                        private val scannerService: ScannerService,
                        private val stepService: StepService,
                        private val runnerExecutionTreeBuilder: RunnerExecutionTreeBuilder,
                        private val variablesService: VariablesService,
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
        val suite: RunnerSuite = runnerExecutionTreeBuilder.createTree(cmdlineParams)
        logRunnerSuite(suite)


        // setup runner services
        val stepsClassLoader: ClassLoader = runnerClassloaderFactory.createStepsClassLoader()
        val testContext = TestContextImpl(stepsClassLoader = stepsClassLoader)
        @Suppress("DEPRECATION")
        run {
            TesterumServiceLocator.registerService(TestVariables::class.java, testVariables)
            TesterumServiceLocator.registerService(SettingsManager::class.java, settingsManager)
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
                variablesService.getVariablesAsMap()
        )

        // execute tests
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
        executionListenerFinder.outputFormat = cmdlineParams.outputFormat

        scannerService.init()
        stepService.loadSteps()
    }


    private fun logRunnerSuite(suite: RunnerSuite) {
        println("------------------------------[ execution tree ]------------------------------")
        print(suite.toString())
        println("------------------------------------------------------------------------------\n")
    }

}

