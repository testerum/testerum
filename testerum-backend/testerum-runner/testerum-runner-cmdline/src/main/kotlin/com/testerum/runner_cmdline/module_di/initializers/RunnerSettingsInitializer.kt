package com.testerum.runner_cmdline.module_di.initializers

import com.testerum.common_jdk.asMap
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.settings.SettingsManager
import com.testerum.settings.getResolvedSettingValues
import com.testerum.settings.keys.SystemSettingKeys
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class RunnerSettingsInitializer(private val cmdlineParams: CmdlineParams) {

    fun initSettings(settingsManager: SettingsManager) {
        val settingsValues = settingsFromCommandLine()

        settingsManager.modify {
            setValues(settingsValues)
        }

        logSettings(settingsManager)
    }

    private fun settingsFromCommandLine(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        result.putAll(systemSettings())
        result.putAll(loadSettingsFromFile())
        result.putAll(cmdlineParams.settingOverrides)

        return result
    }

    private fun loadSettingsFromFile(): Map<String, String> {
        val settingsFile = cmdlineParams.settingsFile
                ?: return emptyMap()

        if (!Files.exists(cmdlineParams.settingsFile)) {
            throw RuntimeException("settings file [${settingsFile.toAbsolutePath().normalize()}] does not exist")
        }

        val properties = Properties()

        Files.newBufferedReader(settingsFile).use {
            properties.load(it)
        }

        return properties.asMap()
    }

    private fun systemSettings(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        val testerumInstallDir = System.getProperty(SystemSettingKeys.TESTERUM_INSTALL_DIR)
                ?.let { Paths.get(it).toAbsolutePath().normalize() }
                ?: throw RuntimeException("the system property ${SystemSettingKeys.TESTERUM_INSTALL_DIR} is missing")

        result[SystemSettingKeys.TESTERUM_INSTALL_DIR] = testerumInstallDir.toString()
        result[SystemSettingKeys.REPOSITORY_DIR] = cmdlineParams.repositoryDirectory.toAbsolutePath().normalize().toString()
        result[SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR] = cmdlineParams.basicStepsDirectory.toAbsolutePath().normalize().toString()
        result[SystemSettingKeys.JDBC_DRIVERS_DIR] = testerumInstallDir.resolve("relational_database_drivers").toString()

        return result
    }

    private fun logSettings(settingsManager: SettingsManager) {
        println("Settings:")
        println("---------")

        for ((key, value) in settingsManager.getResolvedSettingValues()) {
            println("[$key] = [$value]")
        }

        println()
    }

}
