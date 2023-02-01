package com.testerum.runner_cmdline.module_di

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.project_manager.module_di.ProjectManagerModuleFactory
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.module_di.initializers.RunnerSettingsInitializer
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_di.submodules.RunnerTransformersModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory

class RunnerModuleBootstrapper(
    cmdlineParams: RunCmdlineParams,
    stopWatch: StopWatch
) {

    val context = ModuleFactoryContext()

    private val settingsModuleFactory = SettingsModuleFactory(context).also {
        RunnerSettingsInitializer(cmdlineParams)
            .initSettings(it.settingsManager)
    }

    private val runnerTransformersModuleFactory = RunnerTransformersModuleFactory(context)

    val runnerListenersModuleFactory = RunnerListenersModuleFactory(context)

    val fileServiceModuleFactory = FileServiceModuleFactory(context, settingsModuleFactory)

    private val projectManagerModuleFactory = ProjectManagerModuleFactory(
        context,
        fileServiceModuleFactory,
        settingsModuleFactory,
        packagesWithAnnotations = cmdlineParams.packagesWithAnnotations
    )

    val runnerModuleFactory = RunnerModuleFactory(
        context,
        runnerTransformersModuleFactory,
        runnerListenersModuleFactory,
        settingsModuleFactory,
        fileServiceModuleFactory,
        projectManagerModuleFactory,
        cmdlineParams,
        stopWatch
    )

}
