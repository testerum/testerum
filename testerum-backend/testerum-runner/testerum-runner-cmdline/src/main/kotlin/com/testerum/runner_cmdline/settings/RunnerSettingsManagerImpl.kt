package com.testerum.runner_cmdline.settings

import com.testerum.api.test_context.settings.RunnerSettingsManager
import com.testerum.api.test_context.settings.model.Setting
import com.testerum.settings.SettingsManager
import com.testerum.settings.getRequiredSetting

class RunnerSettingsManagerImpl(private val settingsManager: SettingsManager) : RunnerSettingsManager {

    override fun getSettings(): List<Setting> = settingsManager.getSettings()

    override fun getSetting(key: String): Setting? = settingsManager.getSetting(key)

    override fun getRequiredSetting(key: String): Setting = settingsManager.getRequiredSetting(key)

}
