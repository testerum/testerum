package com.testerum.settings.module_factory

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.settings.private_api.SettingsManagerImpl

@Suppress("unused", "LeakingThis")
class SettingsModuleFactory(context: ModuleFactoryContext,
                            settingsManagerFactory: () -> SettingsManagerImpl = { SettingsManagerImpl.factoryMethodForWebPart() }) : BaseModuleFactory(context) {

    val settingsManager = settingsManagerFactory()
    
}
