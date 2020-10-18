@file:Suppress("MemberVisibilityCanBePrivate")

package com.testerum.web_backend.module_di

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.project_manager.module_di.ProjectManagerModuleFactory
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory

class WebBackendModuleBootstrapper {

    val context = ModuleFactoryContext()

    val settingsModuleFactory = SettingsModuleFactory(context)

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    val fileServiceModuleFactory = FileServiceModuleFactory(context, settingsModuleFactory)

    val projectManagerModuleFactory = ProjectManagerModuleFactory(context, fileServiceModuleFactory, settingsModuleFactory, scannerModuleFactory)

    val webBackendModuleFactory = WebBackendModuleFactory(context, settingsModuleFactory, fileServiceModuleFactory, projectManagerModuleFactory)


    //======================================== initialization ========================================//
    init {
        webBackendModuleFactory.webBackendInitializer.initialize()
    }

}
