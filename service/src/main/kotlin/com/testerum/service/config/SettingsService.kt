package com.testerum.service.config

import com.testerum.api.test_context.settings.model.SettingWithValue
import com.testerum.settings.private_api.SettingsManagerImpl

class SettingsService(val settingsManager: SettingsManagerImpl) {

    fun getSettings():List<SettingWithValue> {
        return settingsManager.settings.values.toList()
    }

    fun save(settingWithValues: List<SettingWithValue>): List<SettingWithValue> {
        return settingsManager.save(settingWithValues)
    }
}