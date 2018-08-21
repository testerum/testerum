package com.testerum.service.settings

import com.testerum.api.test_context.settings.model.Setting
import com.testerum.settings.SettingsManager
import com.testerum.settings.getNonDefaultSettings

class SettingsService(private val settingsManager: SettingsManager,
                      private val settingsFileService: SettingsFileService) {

    fun getSettings():List<Setting> {
        return settingsManager.getSettings()
    }

    fun save(settingValues: Map<String, String>): List<Setting> {
        settingsManager.modify {
            setValues(settingValues)
        }

        saveSettingsToFile()

        return settingsManager.getSettings()
    }

    private fun saveSettingsToFile() {
        settingsFileService.saveSettings(
                getSettingsToSave()
        )
    }

    private fun getSettingsToSave(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        for (setting in settingsManager.getNonDefaultSettings()) {
            result[setting.definition.key] = setting.unresolvedValue
        }

        return result
    }

}
