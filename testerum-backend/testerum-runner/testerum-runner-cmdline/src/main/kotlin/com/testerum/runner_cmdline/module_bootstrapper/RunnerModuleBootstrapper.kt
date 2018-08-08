package com.testerum.runner_cmdline.module_bootstrapper

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.resource_manager.module_factory.ResourceManagerModuleFactory
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.module_factory.RunnerModuleFactory
import com.testerum.runner_cmdline.module_factory.factories.SettingsManagerFactory
import com.testerum.runner_cmdline.module_factory.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_factory.submodules.RunnerTransformersModuleFactory
import com.testerum.scanner.step_lib_scanner.module_factory.TesterumScannerModuleFactory
import com.testerum.service.module_factory.ServiceModuleFactory
import com.testerum.service.scanner.ScannerService
import com.testerum.service.step.StepService
import com.testerum.settings.module_factory.SettingsModuleFactory

@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
class RunnerModuleBootstrapper(cmdlineParams: CmdlineParams,
                               stopWatch: StopWatch) {

    val context = ModuleFactoryContext()


    // hack: dependency overridden here to initialize differently
    val settingsModuleFactory = SettingsModuleFactory(
            context,
            settingsManagerFactory = { SettingsManagerFactory(cmdlineParams).createSettingsManger() }
    )

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context, settingsModuleFactory)

    val resourceManagerModuleFactory = ResourceManagerModuleFactory(context, fileRepositoryModuleFactory)

    // hack: dependency overridden here to prevent initialization of steps in a background thread (initialization in the runner should be done after we set the settings, which we get from command line args)
    val serviceModuleFactory = ServiceModuleFactory(
            context,
            scannerModuleFactory,
            resourceManagerModuleFactory,
            fileRepositoryModuleFactory,
            settingsModuleFactory,
            scannerServiceFactory = { settingsManager, stepLibraryCacheManger ->
                ScannerService(settingsManager, stepLibraryCacheManger)
            },
            stepServiceFactory = { basicStepsService, composedStepsService, warningService ->
                StepService(
                        basicStepsService = basicStepsService,
                        composedStepsService = composedStepsService,
                        warningService = warningService
                )
            }
    )
    val runnerTransformersModuleFactory = RunnerTransformersModuleFactory(context)

    val runnerListenersModuleFactory = RunnerListenersModuleFactory(context)

    val runnerModuleFactory = RunnerModuleFactory(context, runnerTransformersModuleFactory, runnerListenersModuleFactory, settingsModuleFactory, serviceModuleFactory, stopWatch)

}