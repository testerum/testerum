package com.testerum.runner_cmdline.module_di

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.project_manager.module_di.ProjectManagerModuleFactory
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.module_di.initializers.RunnerSettingsInitializer
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_di.submodules.RunnerTransformersModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory
import java.nio.file.Path as JavaPath

class RunnerModuleBootstrapper(cmdlineParams: CmdlineParams,
                               stopWatch: StopWatch) {

    val context = ModuleFactoryContext()

    private val settingsModuleFactory = SettingsModuleFactory(context).also {
        RunnerSettingsInitializer(cmdlineParams, it.testerumDirs)
                .initSettings(it.settingsManager)
    }

    private val scannerModuleFactory = TesterumScannerModuleFactory(context)

    private val runnerTransformersModuleFactory = RunnerTransformersModuleFactory(context)

    private val runnerListenersModuleFactory = RunnerListenersModuleFactory(context)

    private val fileServiceModuleFactory = FileServiceModuleFactory(context, settingsModuleFactory, scannerModuleFactory)

    private val projectManagerModuleFactory = ProjectManagerModuleFactory(context, fileServiceModuleFactory)

    val runnerModuleFactory = RunnerModuleFactory(context, runnerTransformersModuleFactory, runnerListenersModuleFactory, settingsModuleFactory, fileServiceModuleFactory, projectManagerModuleFactory, cmdlineParams, stopWatch)

}
