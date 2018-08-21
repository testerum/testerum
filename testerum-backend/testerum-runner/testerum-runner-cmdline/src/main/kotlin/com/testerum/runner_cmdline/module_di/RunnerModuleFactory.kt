package com.testerum.runner_cmdline.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
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
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory

class RunnerModuleFactory(context: ModuleFactoryContext,
                          runnerTransformersModuleFactory: RunnerTransformersModuleFactory,
                          runnerListenersModuleFactory: RunnerListenersModuleFactory,
                          settingsModuleFactory: SettingsModuleFactory,
                          serviceModuleFactory: ServiceModuleFactory,
                          stopWatch: StopWatch) : BaseModuleFactory(context) {

    private val eventsService = EventsService(
            executionListenerFinder = runnerListenersModuleFactory.executionListenerFinder
    )

    private val testerumLogger = TesterumLoggerImpl(
            eventsService = eventsService
    )

    private val runnerClassloaderFactory = RunnerClassloaderFactory(
            settingsManager = settingsModuleFactory.settingsManager
    )

    private val runnerTestsFinder = RunnerTestsFinder()

    private val runnerExecutionTreeBuilder = RunnerExecutionTreeBuilder(
            runnerTestsFinder = runnerTestsFinder,
            hooksService = serviceModuleFactory.hooksService,
            testsService = serviceModuleFactory.testsService
    )

    private val runnerSettingsManager = RunnerSettingsManagerImpl(
            settingsManager = settingsModuleFactory.settingsManager
    )

    val runnerApplication = RunnerApplication(
            runnerClassloaderFactory = runnerClassloaderFactory,
            runnerSettingsManager = runnerSettingsManager,
            eventsService = eventsService,
            scannerService = serviceModuleFactory.scannerService,
            stepCache = serviceModuleFactory.stepService,
            runnerExecutionTreeBuilder = runnerExecutionTreeBuilder,
            variablesService = serviceModuleFactory.variablesService,
            testVariables = TestVariablesImpl,
            executionListenerFinder = runnerListenersModuleFactory.executionListenerFinder,
            globalTransformers = runnerTransformersModuleFactory.globalTransformers,
            testerumLogger = testerumLogger,
            stopWatch = stopWatch
    )

}
