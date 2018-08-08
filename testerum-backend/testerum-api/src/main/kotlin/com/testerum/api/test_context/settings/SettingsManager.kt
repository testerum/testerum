package com.testerum.api.test_context.settings

import com.testerum.api.services.TesterumService
import com.testerum.api.test_context.settings.model.Setting
import com.testerum.api.test_context.settings.model.SettingWithValue

interface SettingsManager : TesterumService {

    fun getSetting(key: String): SettingWithValue?

    fun getSettingValue(key: String): String?

    fun getSettingValueOrDefault(key: String): String?

    fun getSettingValue(setting: Setting): String?

    fun getAllSettings(): List<SettingWithValue>

}