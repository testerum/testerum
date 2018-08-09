package com.testerum.runner_cmdline.module_di.creators

import com.testerum.common_jdk.asMap
import com.testerum.common_jdk.loadPropertiesFrom
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.settings.SystemSettings
import com.testerum.settings.private_api.SettingsManagerImpl
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class SettingsManagerCreator(private val cmdlineParams: CmdlineParams) {

    fun createSettingsManger(): SettingsManagerImpl {
        // todo: separate settings management (map of settings) from settings file management
        val settingsFile: Path = if (cmdlineParams.settingsFile != null) {
            cmdlineParams.settingsFile
        } else {
            Paths.get("/file-that-does-not-exist/${UUID.randomUUID()}")
        }
        val settingsManager = SettingsManagerImpl(settingsFile)

        settingsManager.registerPossibleUnresolvedValues(
                settingsFromCommandLine()
        )

        logSettings(settingsManager)

        return settingsManager
    }

    private fun settingsFromCommandLine(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        result.putAll(systemSettings())
        result.putAll(settingsFromFile())
        result.putAll(cmdlineParams.settingOverrides)

        return result
    }

    private fun systemSettings(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        result[SystemSettings.REPOSITORY_DIRECTORY.key] = cmdlineParams.repositoryDirectory.toAbsolutePath().normalize().toString()
        result[SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY.key] = cmdlineParams.basicStepsDirectory.toAbsolutePath().normalize().toString()
        // todo: user step directory

        return result
    }

    private fun settingsFromFile(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        if (this.cmdlineParams.settingsFile != null) {
            result.putAll(
                    loadPropertiesFrom(this.cmdlineParams.settingsFile)
                            .asMap()
            )
        }

        return result
    }

    private fun logSettings(settingsManager: SettingsManagerImpl) {
        println("Settings:")
        println("---------")
        for ((key, _) in settingsManager.settings) {
            println("[$key] = [${settingsManager.getSettingValueOrDefault(key)}]")
        }
        println()
    }

}