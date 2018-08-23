package com.testerum.web_backend.controller.module_di.initializers

import com.testerum.api.test_context.settings.model.SettingDefinition
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.common_jdk.asMap
import com.testerum.settings.SettingsManager
import com.testerum.settings.SettingsManagerModifier
import com.testerum.settings.getResolvedSettingValues
import com.testerum.settings.keys.SystemSettingKeys
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class WebSettingsManagerInitializer(private val settingsFile: Path) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebSettingsManagerInitializer::class.java)
    }

    fun initSettings(settingsManager: SettingsManager) {
        val settingsValuesFromFile = loadSettingsFromFile()

        settingsManager.modify {
            configureSystemSettings(this)
            setValues(settingsValuesFromFile)
        }

        logSettings(settingsManager)
    }

    private fun configureSystemSettings(modifier: SettingsManagerModifier) {
        val testerumInstallDir = System.getProperty(SystemSettingKeys.TESTERUM_INSTALL_DIR)
                ?.let { Paths.get(it).toAbsolutePath().normalize() }
                ?: throw RuntimeException("the system property ${SystemSettingKeys.TESTERUM_INSTALL_DIR} is missing")

        modifier.registerDefinition(
                SettingDefinition(
                        key = SystemSettingKeys.TESTERUM_INSTALL_DIR,
                        type = SettingType.FILESYSTEM_DIRECTORY,
                        defaultValue = testerumInstallDir.toString(),
                        description = "Directory where Testerum is installed",
                        category = "Application"
                )
        )
        modifier.registerDefinition(
                SettingDefinition(
                        key = SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR,
                        type = SettingType.FILESYSTEM_DIRECTORY,
                        defaultValue = "{{${SystemSettingKeys.TESTERUM_INSTALL_DIR}}}/basic_steps",
                        description = "Directory where the built-in Basic Steps jar files and dependencies are located",
                        category = "Application"
                )
        )
        modifier.registerDefinition(
                SettingDefinition(
                        key = SystemSettingKeys.REPOSITORY_DIR,
                        type = SettingType.FILESYSTEM_DIRECTORY,
                        defaultValue = "",
                        description = "Directory path where all your work will be saved",
                        category = "Application"
                )
        )
        modifier.registerDefinition(
                SettingDefinition(
                        key = SystemSettingKeys.JDBC_DRIVERS_DIR,
                        type = SettingType.FILESYSTEM_DIRECTORY,
                        defaultValue = "{{${SystemSettingKeys.TESTERUM_INSTALL_DIR}}}/relational_database_drivers",
                        description = "Directory where the JDBC Drivers jar files and their descriptors are located",
                        category = "Relational Database"
                )
        )
    }

    private fun loadSettingsFromFile(): Map<String, String> {
        if (!Files.exists(settingsFile)) {
            return emptyMap()
        }

        val properties = Properties()

        Files.newBufferedReader(settingsFile).use {
            properties.load(it)
        }

        return properties.asMap()
    }

    private fun logSettings(settingsManager: SettingsManager) {
        LOGGER.info("Settings:")
        LOGGER.info("---------")

        for ((key, value) in settingsManager.getResolvedSettingValues()) {
            LOGGER.info("[$key] = [$value]")
        }

        LOGGER.info("")
    }

}
