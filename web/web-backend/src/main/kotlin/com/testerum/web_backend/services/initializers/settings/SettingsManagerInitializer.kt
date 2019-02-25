package com.testerum.web_backend.services.initializers.settings

import com.testerum.file_service.file.SettingsFileService
import com.testerum.settings.SettingsManager
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class SettingsManagerInitializer(private val settingsFileService: SettingsFileService,
                                 private val settingsManager: SettingsManager,
                                 private val settingsDir: JavaPath) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SettingsManagerInitializer::class.java)
    }

    fun initialize() {
        LOG.info("initializing settings...")

        val startTimeMillis = System.currentTimeMillis()

        val settingsValuesFromFile = settingsFileService.loadSettings(settingsDir)

        settingsManager.modify {
            setValues(settingsValuesFromFile)
        }

        val endTimeMillis = System.currentTimeMillis()

        LOG.info("loading ${settingsManager.getSettings().size} settings took ${endTimeMillis - startTimeMillis} ms")
    }

}
