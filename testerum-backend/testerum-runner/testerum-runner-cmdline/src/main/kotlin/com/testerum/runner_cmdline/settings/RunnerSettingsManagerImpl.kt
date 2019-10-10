package com.testerum.runner_cmdline.settings

import com.testerum_api.testerum_steps_api.test_context.settings.RunnerSettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.model.Setting
import com.testerum.settings.SettingsManager
import com.testerum.settings.getRequiredSetting

class RunnerSettingsManagerImpl(private val settingsManager: SettingsManager) : RunnerSettingsManager {

    override fun getSetting(key: String): Setting? = settingsManager.getSetting(key)

    override fun getRequiredSetting(key: String): Setting = settingsManager.getRequiredSetting(key)

}
