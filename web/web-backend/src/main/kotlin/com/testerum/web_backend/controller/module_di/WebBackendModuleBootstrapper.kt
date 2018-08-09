@file:Suppress("MemberVisibilityCanBePrivate")

package com.testerum.web_backend.controller.module_di

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.settings.SystemSettings
import com.testerum.settings.module_di.SettingsModuleFactory
import com.testerum.web_backend.controller.module_di.creators.SettingsManagerCreator
import com.testerum.web_backend.controller.module_di.WebBackendModuleFactory
import java.nio.file.Path
import java.nio.file.Paths

class WebBackendModuleBootstrapper {

    val context = ModuleFactoryContext()

    val settingsModuleFactory = SettingsModuleFactory(
            context,
            settingsManagerCreator = { SettingsManagerCreator.createSettingsManager() }
    )

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    private val repositoryDirectory: Path = Paths.get(
            settingsModuleFactory.settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY)
    )

    private val jdbcDriversDirectory: Path = Paths.get(
            settingsModuleFactory.settingsManager.getSettingValue(SystemSettings.JDBC_DRIVERS_DIRECTORY)
    )

    val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context, repositoryDirectory)

    val serviceModuleFactory = ServiceModuleFactory(context, scannerModuleFactory, fileRepositoryModuleFactory, settingsModuleFactory, jdbcDriversDirectory)

    val webBackendModuleFactory = WebBackendModuleFactory(context, serviceModuleFactory, settingsModuleFactory, fileRepositoryModuleFactory)

}