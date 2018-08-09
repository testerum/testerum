package com.testerum.web_backend.controller.module_di.creators

import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.settings.private_api.SettingsManagerImpl
import java.nio.file.Path

object SettingsManagerCreator {

    private val SETTINGS_FILE: Path = SettingsManager.TESTERUM_DIRECTORY.resolve("settings.properties")

    fun createSettingsManager() = SettingsManagerImpl(SETTINGS_FILE)

}