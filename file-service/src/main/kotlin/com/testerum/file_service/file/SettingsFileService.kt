package com.testerum.file_service.file

import com.testerum.common_jdk.asMap
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteIfExists
import com.testerum.common_kotlin.exists
import java.nio.file.Files
import java.util.*
import java.nio.file.Path as JavaPath

class SettingsFileService {

    companion object {
        private const val SETTINGS_FILENAME = "settings.properties"
    }

    fun fileExists(settingsDir: JavaPath): Boolean {
        val settingsFile = settingsDir.resolve(SETTINGS_FILENAME)

        return settingsFile.exists
    }

    fun saveSettings(settingsToSave: Map<String, String>,
                     settingsDir: JavaPath): Map<String, String> {
        val settingsFile = settingsDir.resolve(SETTINGS_FILENAME)
        settingsFile.parent.createDirectories()

        val properties = settingsToSave.toProperties()
        Files.newBufferedWriter(settingsFile).use {
            properties.store(it, null)
        }

        return settingsToSave
    }

    fun loadSettings(settingsDir: JavaPath): Map<String, String> {
        val settingsFile = settingsDir.resolve(SETTINGS_FILENAME)

        if (!Files.exists(settingsFile)) {
            return emptyMap()
        }

        val properties = Properties()

        Files.newBufferedReader(settingsFile).use {
            properties.load(it)
        }

        return properties.asMap()
    }

    fun deleteSettings(settingsDir: JavaPath) {
        val settingsFile = settingsDir.resolve(SETTINGS_FILENAME)

        settingsFile.deleteIfExists()
    }

}
