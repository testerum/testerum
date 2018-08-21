package com.testerum.service.settings

import java.nio.file.Files
import java.nio.file.Path

class SettingsFileService(private val settingsFile: Path) {

    fun fileExists(): Boolean = Files.exists(settingsFile)

    fun saveSettings(settingsToSave: Map<String, String>) {
        val properties = settingsToSave.toProperties()

        Files.createDirectories(settingsFile.parent)

        Files.newBufferedWriter(settingsFile).use {
            properties.store(it, null)
        }
    }

}
