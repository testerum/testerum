package com.testerum.runner_cmdline.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.runner_cmdline.RunnerApplication
import com.testerum.runner_cmdline.classloader.RunnerClassloaderFactory
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.logger.TesterumLoggerImpl
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_di.submodules.RunnerTransformersModuleFactory
import com.testerum.runner_cmdline.runner_tree.builder.RunnerExecutionTreeBuilder
import com.testerum.runner_cmdline.runner_tree.vars_context.TestVariablesImpl
import com.testerum.runner_cmdline.settings.RunnerSettingsManagerImpl
import com.testerum.runner_cmdline.tests_finder.RunnerTestsFinder
import com.testerum.settings.module_di.SettingsModuleFactory

class RunnerModuleFactory(context: ModuleFactoryContext,
                          runnerTransformersModuleFactory: RunnerTransformersModuleFactory,
                          runnerListenersModuleFactory: RunnerListenersModuleFactory,
                          settingsModuleFactory: SettingsModuleFactory,
                          fileServiceModuleFactory: FileServiceModuleFactory,

                          stopWatch: StopWatch) : BaseModuleFactory(context) {

    val eventsService = EventsService(
            executionListenerFinder = runnerListenersModuleFactory.executionListenerFinder
    ).apply {
        context.registerShutdownHook {
            stop()
        }
    }

    private val testerumLogger = TesterumLoggerImpl(
            eventsService = eventsService
    )

    private val runnerClassloaderFactory = RunnerClassloaderFactory(
            settingsManager = settingsModuleFactory.settingsManager
    )

    private val runnerTestsFinder = RunnerTestsFinder()

    private val runnerExecutionTreeBuilder = RunnerExecutionTreeBuilder(
            runnerTestsFinder = runnerTestsFinder,
            stepsCache = fileServiceModuleFactory.stepsCache,
            testsCache = fileServiceModuleFactory.testsCache
    )

    private val runnerSettingsManager = RunnerSettingsManagerImpl(
            settingsManager = settingsModuleFactory.settingsManager
    )

    val runnerApplication = RunnerApplication(
            runnerClassloaderFactory = runnerClassloaderFactory,
            runnerSettingsManager = runnerSettingsManager,
            settingsManager = settingsModuleFactory.settingsManager,
            eventsService = eventsService,
            stepsCache = fileServiceModuleFactory.stepsCache,
            testsCache = fileServiceModuleFactory.testsCache,
            featuresCache = fileServiceModuleFactory.featuresCache,
            runnerExecutionTreeBuilder = runnerExecutionTreeBuilder,
            variablesFileService = fileServiceModuleFactory.variablesFileService,
            testVariables = TestVariablesImpl,
            executionListenerFinder = runnerListenersModuleFactory.executionListenerFinder,
            globalTransformers = runnerTransformersModuleFactory.globalTransformers,
            testerumLogger = testerumLogger,
            stopWatch = stopWatch
    )

}
