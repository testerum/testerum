package com.testerum.launcher.config

import java.nio.file.Paths
import java.nio.file.Path as JavaPath

object PathsManager {

    private val TESTERUM_ROOT_DIR_SYSTEM_PROP = "testerumRootDir"

    val testerumRootDir: JavaPath = run {
        val sysPropValue = System.getProperty(TESTERUM_ROOT_DIR_SYSTEM_PROP)
                ?: throw Exception("The system property [$TESTERUM_ROOT_DIR_SYSTEM_PROP] was not specified")

        Paths.get(sysPropValue).toAbsolutePath().normalize()
    }

    private val testerumUserDir: JavaPath = Paths.get(System.getProperty("user.home")).resolve(".testerum")

    private val settingsDir: JavaPath = testerumUserDir.resolve("conf")

    val configFilePath: JavaPath = settingsDir.resolve("testerum-launcher.properties").toAbsolutePath().normalize()

    val logConfigFilePath: JavaPath = testerumRootDir.resolve("conf/logback.xml").toAbsolutePath().normalize()

    val classpathRepoDir: JavaPath = testerumRootDir.resolve("repo").toAbsolutePath().normalize()
}