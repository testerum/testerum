package com.testerum.launcher.config

import com.testerum.launcher.config.model.Config
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.nio.file.Path as JavaPath

class ConfigManager {

    companion object {
        private val CONFIG_FILE_LOCATION_SYSTEM_PROP = "configFileLocation"
        private val HTTP_PORT_PROP = "testerum.web.httpPort"
    }

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

    private fun getConfigFilePath(): JavaPath {
        val sysPropValue: String = System.getProperty(CONFIG_FILE_LOCATION_SYSTEM_PROP)
                ?: throw Exception("The system property [$CONFIG_FILE_LOCATION_SYSTEM_PROP] was not specified")

        return Paths.get(sysPropValue).toAbsolutePath().normalize()
    }
}