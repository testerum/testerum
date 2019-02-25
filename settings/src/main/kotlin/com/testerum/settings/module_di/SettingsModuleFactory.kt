package com.testerum.settings.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.settings.SettingsManager
import com.testerum.settings.TesterumDirs

class SettingsModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val settingsManager = SettingsManager()

    val testerumDirs = TesterumDirs()

}
