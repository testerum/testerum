package com.testerum.settings

import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class TesterumDirs {

    fun getInstallDir(): JavaPath = run {
        val systemPropertyName = "testerum.packageDirectory"

        System.getProperty(systemPropertyName)
                ?.let { Paths.get(it).toAbsolutePath().normalize() }
                ?: throw RuntimeException("the system property $systemPropertyName is missing")
    }

    fun getFsNotifierBinariesDir(): JavaPath = getInstallDir().resolve("fsnotifier")

    fun getRunnerDir(): JavaPath = getInstallDir().resolve("runner")

    fun getTesterumDir(): JavaPath = Paths.get(System.getProperty("user.home")).resolve(".testerum")

    fun getSettingsDir(): JavaPath = getTesterumDir().resolve("conf")
    fun getFileLocalVariablesFile(): JavaPath = getSettingsDir().resolve("variables.json")

    fun getBasicStepsDir(): JavaPath = getInstallDir().resolve("basic_steps")
    fun getJdbcDriversDir(): JavaPath = getInstallDir().resolve("relational_database_drivers")

}
