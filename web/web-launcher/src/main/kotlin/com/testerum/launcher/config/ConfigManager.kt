package com.testerum.launcher.config

import com.testerum.launcher.config.model.Config
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ConfigManager {
    val CONFIG_FILE_LOCATION_VM_PROP = "configFileLocation"
    val HTTP_PORT_PROP = "testerum.web.httpPort"

    fun getConfig(): Config {
        val configFilePath = getConfigFilePath()
        val properties = Properties()
        properties.load(Files.newInputStream(configFilePath))

        val httpPort = properties.getProperty(HTTP_PORT_PROP)
        return Config(httpPort.toInt())
    }

    fun saveConfig(config: Config) {
        val properties = Properties()
        properties.setProperty(HTTP_PORT_PROP, config.port.toString())
        properties.store(Files.newBufferedWriter(getConfigFilePath()), null)
    }

    private fun getConfigFilePath(): Path? {
        val configFileLocation: String = System.getProperty(CONFIG_FILE_LOCATION_VM_PROP)
                ?: throw Exception("The VM property [${CONFIG_FILE_LOCATION_VM_PROP}] was not specified")

        val configFilePath = Paths.get(configFileLocation)
        return configFilePath
    }
}