package com.testerum.launcher.config

import com.testerum.launcher.config.model.Config
import java.nio.file.Files
import java.util.*
import java.nio.file.Path as JavaPath

object ConfigManager {

    private val HTTP_PORT_PROP = "testerum.web.httpPort"

    fun getConfig(): Config {
        val properties = Properties()
        properties.load(
                Files.newInputStream(PathsManager.configFilePath)
        )

        val httpPort = properties.getProperty(HTTP_PORT_PROP)

        return Config(httpPort.toInt())
    }

    fun saveConfig(config: Config) {
        val properties = Properties()
        properties.setProperty(HTTP_PORT_PROP, config.port.toString())
        properties.store(
                Files.newBufferedWriter(PathsManager.configFilePath),
                null
        )
    }

}