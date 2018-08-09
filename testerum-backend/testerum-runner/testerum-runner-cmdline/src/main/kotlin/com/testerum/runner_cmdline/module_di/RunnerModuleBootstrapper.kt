package com.testerum.runner_cmdline.module_di

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.module_di.creators.SettingsManagerCreator
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_di.submodules.RunnerTransformersModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.service.scanner.ScannerService
import com.testerum.service.step.StepService
import com.testerum.settings.SystemSettings
import com.testerum.settings.module_di.SettingsModuleFactory
import java.nio.file.Path
import java.nio.file.Paths

class RunnerModuleBootstrapper(cmdlineParams: CmdlineParams,
                               stopWatch: StopWatch) {

    val context = ModuleFactoryContext()

    // hack: dependency overridden here to initialize differently (to get repository directory & other settings from command line parameters)
    private val settingsModuleFactory = SettingsModuleFactory(
            context,
            settingsManagerCreator = { SettingsManagerCreator(cmdlineParams).createSettingsManger() }
    )

    private val scannerModuleFactory = TesterumScannerModuleFactory(context)

    private val repositoryDirectory: Path = Paths.get(
            settingsModuleFactory.settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY)
    )

    private val jdbcDriversDirectory: Path = Paths.get(
            settingsModuleFactory.settingsManager.getSettingValue(SystemSettings.JDBC_DRIVERS_DIRECTORY)
    )

    private val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context, repositoryDirectory)

    // hack: dependency overridden here to prevent initialization of steps in a background thread (initialization in the runner should be done after we set the settings, which we get from command line args)
    private val serviceModuleFactory = ServiceModuleFactory(
            context,
            scannerModuleFactory,
            fileRepositoryModuleFactory,
            settingsModuleFactory,
            jdbcDriversDirectory,
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

    private val runnerTransformersModuleFactory = RunnerTransformersModuleFactory(context)

    private val runnerListenersModuleFactory = RunnerListenersModuleFactory(context)

    val runnerModuleFactory = RunnerModuleFactory(context, runnerTransformersModuleFactory, runnerListenersModuleFactory, settingsModuleFactory, serviceModuleFactory, stopWatch)

}