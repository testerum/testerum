@file:Suppress("MemberVisibilityCanBePrivate")

package com.testerum.web_backend.controller.module_bootstrapper

import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.resource_manager.module_factory.ResourceManagerModuleFactory
import com.testerum.scanner.step_lib_scanner.module_factory.TesterumScannerModuleFactory
import com.testerum.service.module_factory.ServiceModuleFactory
import com.testerum.settings.module_factory.SettingsModuleFactory
import com.testerum.web_backend.controller.module_factory.WebBackendModuleFactory

@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
class WebBackendModuleBootstrapper {

    val context = ModuleFactoryContext()

    val settingsModuleFactory = SettingsModuleFactory(context)

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context, settingsModuleFactory)

    val resourceManagerModuleFactory = ResourceManagerModuleFactory(context, fileRepositoryModuleFactory)

    val serviceModuleFactory = ServiceModuleFactory(context, scannerModuleFactory, resourceManagerModuleFactory, fileRepositoryModuleFactory, settingsModuleFactory)

    val webBackendModuleFactory = WebBackendModuleFactory(context, serviceModuleFactory, settingsModuleFactory, fileRepositoryModuleFactory)

}