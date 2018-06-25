package com.testerum.runner

import com.testerum.runner.cmdline.params.model.CmdlineParams
import com.testerum.settings.private_api.SettingsManagerImpl
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object SettingsManagerFactory {

    lateinit var cmdlineParams: CmdlineParams

    @JvmStatic
    fun createSettingsManger(): SettingsManagerImpl {
        val settingsManager = SettingsManagerImpl()

        settingsManager.registerPossibleUnresolvedValues(
                cmdlineSettings()
        )

        return settingsManager
    }

    private fun cmdlineSettings(): Map<String, String?> {
        val result = mutableMapOf<String, String?>()

        val cmdlineParams: CmdlineParams = cmdlineParams

        if (cmdlineParams.settingsFile != null) {
            result.putAll(
                    loadSettingsFile(cmdlineParams.settingsFile)
            )
        }

        result.putAll(cmdlineParams.settingOverrides)

        return result
    }

    private fun loadSettingsFile(settingsFile: Path): Map<out String, String?> {
        val properties: Properties = Files.newInputStream(settingsFile).use { propertiesInputStream ->
            Properties().apply {
                load(propertiesInputStream)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return properties as Map<String, String>
    }

}