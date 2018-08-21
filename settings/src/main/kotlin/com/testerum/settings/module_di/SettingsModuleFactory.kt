package com.testerum.settings.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.settings.SettingsManager

class SettingsModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val settingsManager = SettingsManager()
    
}
