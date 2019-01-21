package com.testerum.web_backend.services.initializers.settings

import com.testerum.api.test_context.settings.model.SettingDefinition
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.file_service.file.SettingsFileService
import com.testerum.settings.SettingsManager
import com.testerum.settings.SettingsManagerModifier
import com.testerum.settings.TesterumDirs
import com.testerum.settings.keys.SystemSettingKeys
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class SettingsManagerInitializer(private val settingsFileService: SettingsFileService,
                                 private val settingsManager: SettingsManager,
                                 private val testerumDirs: TesterumDirs,
                                 private val settingsDir: JavaPath) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SettingsManagerInitializer::class.java)
    }

    fun initialize() {
        LOG.info("initializing settings...")

        val startTimeMillis = System.currentTimeMillis()

        val settingsValuesFromFile = settingsFileService.loadSettings(settingsDir)

        settingsManager.modify {
            configureSystemSettings(this)
            setValues(settingsValuesFromFile)
        }

        val endTimeMillis = System.currentTimeMillis()

        LOG.info("loading ${settingsManager.getSettings().size} settings took ${endTimeMillis - startTimeMillis} ms")
    }

    private fun configureSystemSettings(modifier: SettingsManagerModifier) {
        modifier.registerDefinition(
                SettingDefinition(
                        key = SystemSettingKeys.REPOSITORY_DIR,
                        type = SettingType.FILESYSTEM_DIRECTORY,
                        defaultValue = "",
                        description = "Directory path where all your work will be saved",
                        category = "Application"
                )
        )
    }


}
