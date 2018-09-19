@file:Suppress("MemberVisibilityCanBePrivate")

package com.testerum.web_backend.module_di

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory
import java.nio.file.Path as JavaPath

class WebBackendModuleBootstrapper {

    val context = ModuleFactoryContext()

    val settingsModuleFactory = SettingsModuleFactory(context)

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    val fileServiceModuleFactory = FileServiceModuleFactory(context, settingsModuleFactory, scannerModuleFactory)

    val webBackendModuleFactory = WebBackendModuleFactory(context, settingsModuleFactory, fileServiceModuleFactory)


    //======================================== initialization ========================================//
    init {
        webBackendModuleFactory.webBackendInitializer.initialize()
    }

}
