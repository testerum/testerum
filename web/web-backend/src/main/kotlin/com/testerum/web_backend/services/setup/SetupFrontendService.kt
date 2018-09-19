package com.testerum.web_backend.services.setup

import com.testerum.file_service.file.SettingsFileService
import com.testerum.settings.SettingsManager
import com.testerum.settings.hasValue
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.initializers.WebBackendInitializer
import java.nio.file.Path as JavaPath

class SetupFrontendService(private val frontendDirs: FrontendDirs,
                           private val settingsManager: SettingsManager,
                           private val settingsFileService: SettingsFileService,
                           private val webBackendInitializer: WebBackendInitializer) {

    fun isSetupCompleted(): Boolean {
        val settingsDir = frontendDirs.getSettingsDir()

        return settingsFileService.fileExists(settingsDir)
                && settingsManager.hasValue(SystemSettingKeys.REPOSITORY_DIR)
    }

    fun createConfig(repositoryPath: JavaPath) {
        val settingsDir = frontendDirs.getSettingsDir()
        val settingsToSave = mapOf(
                SystemSettingKeys.REPOSITORY_DIR to repositoryPath.toAbsolutePath().normalize().toString()
        )
        settingsFileService.saveSettings(settingsToSave, settingsDir)

        try {
            webBackendInitializer.initialize()
        } catch (e: Exception) {
            settingsFileService.deleteSettings(settingsDir)

            throw e
        }
    }

}
