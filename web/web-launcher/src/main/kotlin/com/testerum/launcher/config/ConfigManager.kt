package com.testerum.launcher.config

import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.doesNotExist
import com.testerum.launcher.config.model.Config
import java.nio.file.Files
import java.util.*
import java.nio.file.Path as JavaPath

object ConfigManager {

    private val HTTP_PORT_PROP = "testerum.web.httpPort"

    fun getConfig(): Config {
        if (PathsManager.configFilePath.doesNotExist) {
            return Config.DEFAULT
        }

        val properties = Properties()
        Files.newInputStream(PathsManager.configFilePath).use {
            properties.load(it)
        }

        val httpPort = properties.getProperty(HTTP_PORT_PROP)

        return Config(httpPort.toInt())
    }

    fun saveConfig(config: Config) {
        val properties = Properties()
        properties.setProperty(HTTP_PORT_PROP, config.port.toString())

        PathsManager.configFilePath.parent?.createDirectories()

        properties.store(
                Files.newBufferedWriter(PathsManager.configFilePath),
                null
        )
    }

}