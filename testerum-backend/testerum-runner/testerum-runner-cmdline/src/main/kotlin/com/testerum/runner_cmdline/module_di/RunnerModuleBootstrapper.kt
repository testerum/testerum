package com.testerum.runner_cmdline.module_di

import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.module_di.initializers.RunnerSettingsInitializer
import com.testerum.runner_cmdline.module_di.submodules.RunnerListenersModuleFactory
import com.testerum.runner_cmdline.module_di.submodules.RunnerTransformersModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.service.step.StepCache
import com.testerum.settings.getRequiredSetting
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.settings.module_di.SettingsModuleFactory
import java.nio.file.Path

class RunnerModuleBootstrapper(cmdlineParams: CmdlineParams,
                               stopWatch: StopWatch) {

    val context = ModuleFactoryContext()

    private val settingsModuleFactory = SettingsModuleFactory(context).also {
        RunnerSettingsInitializer(cmdlineParams)
                .initSettings(it.settingsManager)
    }

    private val scannerModuleFactory = TesterumScannerModuleFactory(context)

    private val repositoryDirectory: Path = settingsModuleFactory.settingsManager.getRequiredSetting(SystemSettingKeys.REPOSITORY_DIR).resolvedValueAsPath

    private val jdbcDriversDirectory: Path = settingsModuleFactory.settingsManager.getRequiredSetting(SystemSettingKeys.JDBC_DRIVERS_DIR).resolvedValueAsPath

    private val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context) { repositoryDirectory }

    // hack: dependency overridden here to prevent initialization of steps in a background thread (initialization in the runner should be done after we set the settings, which we get from command line args)
    private val serviceModuleFactory = ServiceModuleFactory(
            context,
            scannerModuleFactory,
            fileRepositoryModuleFactory,
            settingsModuleFactory,
            jdbcDriversDirectory,
            stepServiceFactory = { basicStepsService, composedStepsService, warningService ->
                StepCache(
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
