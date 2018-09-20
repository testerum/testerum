package com.testerum.web_backend.services.settings

import com.testerum.api.test_context.settings.model.Setting
import com.testerum.file_service.file.SettingsFileService
import com.testerum.settings.SettingsManager
import com.testerum.settings.getNonDefaultSettings
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.initializers.caches.CachesInitializer

class SettingsFrontendService(private val frontendDirs: FrontendDirs,
                              private val settingsManager: SettingsManager,
                              private val settingsFileService: SettingsFileService,
                              private val cachesInitializer: CachesInitializer) {

    fun getSettings(): List<Setting> = settingsManager.getSettings()

    fun saveSettings(settingValues: Map<String, String>): List<Setting> {
        settingsManager.modify {
            setValues(settingValues)
        }

        saveSettingsToFile()
        cachesInitializer.initialize()

        return settingsManager.getSettings()
    }

    private fun saveSettingsToFile() {
        val settingsToSave = getSettingsToSave()
        val settingsDir = frontendDirs.getSettingsDir()

        settingsFileService.saveSettings(settingsToSave, settingsDir)
    }

    private fun getSettingsToSave(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        for (setting in settingsManager.getNonDefaultSettings()) {
            result[setting.definition.key] = setting.unresolvedValue
        }

        return result
    }

}
