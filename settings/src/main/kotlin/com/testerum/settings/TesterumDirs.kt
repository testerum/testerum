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

    fun getRunnerDir(): JavaPath = getInstallDir().resolve("runner")

    fun getBasicStepsDir(): JavaPath = getInstallDir().resolve("basic_steps")
    fun getJdbcDriversDir(): JavaPath = getInstallDir().resolve("relational_database_drivers")

}
