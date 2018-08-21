package com.testerum.service.settings

import com.testerum.settings.SettingsManager
import com.testerum.settings.hasValue
import com.testerum.settings.keys.SystemSettingKeys
import java.nio.file.Path

class SetupService(private val settingsFileService: SettingsFileService,
                   private val settingsManager: SettingsManager,
                   private val settingsService: SettingsService) {

    fun isSetupCompleted(): Boolean = settingsFileService.fileExists() && settingsManager.hasValue(SystemSettingKeys.REPOSITORY_DIR)

    fun createConfig(repositoryPath: Path) {
        settingsService.save(
                mapOf(
                        SystemSettingKeys.REPOSITORY_DIR to repositoryPath.toAbsolutePath().normalize().toString()
                )
        )
    }

}
