package com.testerum.settings.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.settings.private_api.SettingsManagerImpl

class SettingsModuleFactory(context: ModuleFactoryContext,
                            settingsManagerCreator: () -> SettingsManagerImpl) : BaseModuleFactory(context) {

    val settingsManager = settingsManagerCreator()
    
}
