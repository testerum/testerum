package com.testerum.api.test_context.settings

import com.testerum.api.services.TesterumService
import com.testerum.api.test_context.settings.model.Setting
import com.testerum.api.test_context.settings.model.SettingWithValue
import java.nio.file.Path
import java.nio.file.Paths

interface SettingsManager : TesterumService {

    companion object {
        val TESTERUM_DIRECTORY: Path = Paths.get(System.getProperty("user.home") + "/.testerum")
    }

    fun getSetting(key: String): SettingWithValue?

    fun getSettingValue(key: String): String?

    fun getSettingValueOrDefault(key: String): String?

    fun getSettingValue(setting: Setting): String?

    fun getAllSettings(): List<SettingWithValue>

}