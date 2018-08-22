@file:Suppress("MemberVisibilityCanBePrivate")

package com.testerum.web_backend.controller.module_di

import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.service.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.settings.getRequiredSetting
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.settings.module_di.SettingsModuleFactory
import com.testerum.web_backend.controller.module_di.initializers.WebSettingsManagerInitializer
import java.nio.file.Path
import java.nio.file.Paths

class WebBackendModuleBootstrapper {

    val context = ModuleFactoryContext()

    private val settingsFile: Path = Paths.get(System.getProperty("user.home")).resolve(".testerum/settings.properties")

    val settingsModuleFactory = SettingsModuleFactory(context).also {
        WebSettingsManagerInitializer(settingsFile).initSettings(it.settingsManager)
    }

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    private val getRepositoryDirectory: () -> Path? = {
        settingsModuleFactory.settingsManager.getSetting(SystemSettingKeys.REPOSITORY_DIR)?.resolvedValueAsPath
    }

    private val jdbcDriversDirectory: Path = settingsModuleFactory.settingsManager.getRequiredSetting(SystemSettingKeys.JDBC_DRIVERS_DIR).resolvedValueAsPath

    val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context, getRepositoryDirectory)

    val serviceModuleFactory = ServiceModuleFactory(context, scannerModuleFactory, fileRepositoryModuleFactory, settingsModuleFactory, jdbcDriversDirectory)

    val webBackendModuleFactory = WebBackendModuleFactory(context, settingsModuleFactory, serviceModuleFactory, fileRepositoryModuleFactory, settingsFile)

    init {
        Thread(Runnable {
            serviceModuleFactory.scannerService.init()
        }).start()
    }

}
