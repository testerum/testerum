package com.testerum.runner_cmdline.module_di.initializers

import com.testerum.common_jdk.asMap
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.settings.SettingsManager
import com.testerum.settings.getResolvedSettingValues
import java.nio.file.Files
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

    private fun logSettings(settingsManager: SettingsManager) {
        println("Settings:")
        println("---------")

        for ((key, value) in settingsManager.getResolvedSettingValues()) {
            println("[$key] = [$value]")
        }

        println()
    }

}
