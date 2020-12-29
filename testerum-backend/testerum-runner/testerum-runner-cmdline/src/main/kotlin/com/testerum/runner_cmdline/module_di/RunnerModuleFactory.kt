package com.testerum.runner_cmdline.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.project_manager.module_di.ProjectManagerModuleFactory
import com.testerum.runner_cmdline.RunnerApplication
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.events.EventsService
import com.testerum.runner_cmdline.runner_api_services.TesterumLoggerImpl
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_di.submodules.RunnerTransformersModuleFactory
import com.testerum.runner_cmdline.project_manager.RunnerProjectManager
import com.testerum.runner_cmdline.runner_tree.RunnerExecutionTreeBuilder
import com.testerum.runner_cmdline.settings.RunnerSettingsManagerImpl
import com.testerum.runner_cmdline.settings.RunnerTesterumDirsImpl
import com.testerum.settings.module_di.SettingsModuleFactory

class RunnerModuleFactory(
    context: ModuleFactoryContext,
    runnerTransformersModuleFactory: RunnerTransformersModuleFactory,
    runnerListenersModuleFactory: RunnerListenersModuleFactory,
    settingsModuleFactory: SettingsModuleFactory,
    fileServiceModuleFactory: FileServiceModuleFactory,
    projectManagerModuleFactory: ProjectManagerModuleFactory,
    cmdlineParams: RunCmdlineParams,
    stopWatch: StopWatch
) : BaseModuleFactory(context) {

    private val runnerProjectManager = RunnerProjectManager(
        projectManager = projectManagerModuleFactory.projectManager,
        projectRootDir = cmdlineParams.repositoryDirectory
    )

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

    private val runnerExecutionTreeBuilder = RunnerExecutionTreeBuilder(
        runnerProjectManager = runnerProjectManager,
        executionName = cmdlineParams.executionName
    )

    private val runnerSettingsManager = RunnerSettingsManagerImpl(
        settingsManager = settingsModuleFactory.settingsManager
    )

    private val runnerTesterumDirs = RunnerTesterumDirsImpl(
        testerumDirs = settingsModuleFactory.testerumDirs
    )

    val runnerApplication = RunnerApplication(
        runnerProjectManager = runnerProjectManager,
        runnerSettingsManager = runnerSettingsManager,
        runnerTesterumDirs = runnerTesterumDirs,
        testerumDirs = settingsModuleFactory.testerumDirs,
        eventsService = eventsService,
        runnerExecutionTreeBuilder = runnerExecutionTreeBuilder,
        variablesFileService = fileServiceModuleFactory.variablesFileService,
        executionListenerFinder = runnerListenersModuleFactory.executionListenerFinder,
        globalTransformers = runnerTransformersModuleFactory.globalTransformers,
        testerumLogger = testerumLogger,
        stopWatch = stopWatch
    )

}
