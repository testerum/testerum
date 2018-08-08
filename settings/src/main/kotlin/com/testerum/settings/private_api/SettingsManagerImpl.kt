package com.testerum.settings.private_api

import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.api.test_context.settings.model.Setting
import com.testerum.api.test_context.settings.model.SettingWithValue
import com.testerum.common_jdk.asMap
import com.testerum.common_jdk.loadPropertiesFrom
import com.testerum.settings.SystemSettings
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

class SettingsManagerImpl : SettingsManager {

    companion object {
        val settingsDirectory: Path = Paths.get(System.getProperty("user.home") + "/.testerum")
        private val settingsFile: Path = settingsDirectory.resolve("settings.properties")

        @JvmStatic
        fun factoryMethodForWebPart(): SettingsManagerImpl {
            val instance = SettingsManagerImpl()

            if (Files.exists(settingsFile)) {
                val properties: Properties = loadPropertiesFrom(settingsFile)

                instance.registerPossibleUnresolvedValues(
                        properties.asMap()
                )
            }

            return instance
        }
    }

    private val propRefPattern = Pattern.compile("""\{\{(.*?)(?=}})}}""")
    var settings:Map<String, SettingWithValue> = hashMapOf()
    var possibleSettingUnresolvedValue: MutableMap<String, String?> = mutableMapOf()

    init {
        registerSettings(
                SystemSettings.allSystemSettings
        )
    }

    override fun getSetting(key: String): SettingWithValue? {
        return settings[key]
    }

    override fun getSettingValue(key: String): String? {
        return settings[key]?.value
    }

    override fun getSettingValueOrDefault(key: String): String? {
        val settingWithValue: SettingWithValue = settings[key]
                ?: return null

        return settingWithValue.value ?: settingWithValue.setting.defaultValue
    }

    override fun getSettingValue(setting: Setting): String? {
        return getSettingValue(setting.key)
    }

    override fun getAllSettings(): List<SettingWithValue>
            = ArrayList(settings.values)

    fun registerSettings(settingsToRegister: List<Setting>) {
        val settingsCopy: MutableMap<String, SettingWithValue> = settings.toMutableMap()

        for (settingToRegister in settingsToRegister) {
            if (settingsCopy.containsKey(settingToRegister.key)) {
                continue
            }

            settingsCopy.put(
                    settingToRegister.key,
                    SettingWithValue(
                            setting = settingToRegister,
                            unresolvedValue = possibleSettingUnresolvedValue[settingToRegister.key],
                            value = null
                    )
            )
        }

        settings = resolveSettings(settingsCopy.values.toMutableList())
    }

    fun registerPossibleUnresolvedValues(settingsValues: Map<String, String?>) {
        possibleSettingUnresolvedValue.putAll(settingsValues)

        val settingsCopy: MutableMap<String, SettingWithValue> = settings.toMutableMap()
        for (setting in settingsCopy) {
            setting.setValue(
                    setting.value.copy(
                            unresolvedValue = settingsValues[setting.key],
                            value = null
                    )
            )
        }

        settings = resolveSettings(settingsCopy.values.toMutableList())
    }

    private fun resolveSettings(unresolvedSettingWithValues: MutableList<SettingWithValue>): MutableMap<String, SettingWithValue> {
        val unresolvedNewSettingWithValues: List<SettingWithValue> = resolveValueAsDefaultValueIfIsNull(unresolvedSettingWithValues)

        val keyValueSettings: Map<String, String?> = unresolvedNewSettingWithValues.map { it.setting.key to it.unresolvedValue }.toMap()
        val resolvedKeyValueSettings: Map<String, String?> = resolveValues(keyValueSettings)

        val resolvedSettings: MutableMap<String, SettingWithValue> = mutableMapOf()
        for (resolvedKeyValueSetting in resolvedKeyValueSettings) {
            resolvedSettings.put(
                    resolvedKeyValueSetting.key,
                    unresolvedNewSettingWithValues.first { it.setting.key == resolvedKeyValueSetting.key }.copy(value = resolvedKeyValueSetting.value)
            )
        }
        return resolvedSettings
    }

    private fun resolveValueAsDefaultValueIfIsNull(settingsWithValue: Collection<SettingWithValue>): List<SettingWithValue> {
        val result: MutableList<SettingWithValue> = mutableListOf()
        for (settingWithValue in settingsWithValue) {
            if (settingWithValue.unresolvedValue == null) {
                result.add(
                        settingWithValue.copy(unresolvedValue = settingWithValue.setting.defaultValue)
                )
            } else {
                result.add(settingWithValue)
            }
        }

        return result
    }

    private fun resolveValues(keyValueMap: Map<String, String?>): Map<String, String?> {
        val resolvedKeyValueMap = keyValueMap.toMutableMap()

        var allPropertiesRefAreResolved = false
        while (!allPropertiesRefAreResolved) {
            val propertyToResolve = getPropertyToResolve(resolvedKeyValueMap)
            if (propertyToResolve == null) {
                allPropertiesRefAreResolved = true
                continue
            }
            val propertyToResolveKey = propertyToResolve.first
            val propertyToResolveValue = propertyToResolve.second
            val variableToResolve: String = propRefPattern.toRegex().find(propertyToResolveValue)!!.groupValues[1]
            val variableToResolveValue = resolvedKeyValueMap[variableToResolve]
            if (variableToResolveValue == null) {
                throw RuntimeException("Exception appeared while loading the system properties. No property with the key [$variableToResolve] was defined")
            }

            val escapedVariableToResolveValue = Regex.escapeReplacement(variableToResolveValue)
            val resolvedValue = propertyToResolveValue.replace(propRefPattern.toRegex(), escapedVariableToResolveValue)
            resolvedKeyValueMap.put(propertyToResolveKey, resolvedValue)
        }
        return resolvedKeyValueMap
    }

    private fun getPropertyToResolve(keyValueMap: Map<String, String?>): Pair<String, String>? {
        for (keyValueEntry in keyValueMap) {
            val valueAsString = keyValueEntry.value
            if(valueAsString != null && propRefPattern.matcher(valueAsString).find()) {
                return Pair<String, String>(keyValueEntry.key, valueAsString)
            }
        }
        return null
    }

    fun isConfigSet(): Boolean {
        return Files.exists(settingsFile)
                && settings[SystemSettings.REPOSITORY_DIRECTORY.key]?.value != null
    }

    fun createConfig(repositoryPath: Path) {
        if (!Files.exists(settingsFile.parent)) {
            Files.createDirectories(settingsFile.parent)
        }

        val propertiesMap: MutableMap<String, String> = mutableMapOf(SystemSettings.REPOSITORY_DIRECTORY.key to repositoryPath.toString())
        saveToPropertyFile(propertiesMap)

        registerPossibleUnresolvedValues(propertiesMap)
    }

    private fun saveToPropertyFile(propertiesMap: MutableMap<String, String>) {

        val properties = Properties()
        properties.putAll(propertiesMap)

        Files.newBufferedWriter(settingsFile, Charsets.UTF_8).use { writer ->
            properties.store(writer, "")
        }
    }

    fun save(settingWithValues: List<SettingWithValue>): List<SettingWithValue> {

        val settingsWithValueToSave: MutableList<SettingWithValue> = mutableListOf()
        for (settingWithValue in settingWithValues) {
            var settingWithValueToSave: SettingWithValue = settingWithValue.copy(value = null)

            if(settingWithValue.setting.key == SystemSettings.REPOSITORY_DIRECTORY.key) {
                if (settingWithValue.unresolvedValue == null) {
                    settingWithValueToSave = settingWithValue.copy(
                            unresolvedValue = this.getSetting(SystemSettings.REPOSITORY_DIRECTORY.key)!!.unresolvedValue
                    )
                } else {
                    settingWithValueToSave = settingWithValue
                }
            }

            if(settingWithValue.unresolvedValue != settingWithValue.setting.defaultValue) {
                settingsWithValueToSave.add(
                        settingWithValueToSave
                )
            }
        }

        val settingsAsPropMap: MutableMap<String, String> = mutableMapOf()
        for (settingWithValue in settingsWithValueToSave) {
            val unresolvedValue = settingWithValue.unresolvedValue
            if (unresolvedValue != null) {
                settingsAsPropMap.put(settingWithValue.setting.key, unresolvedValue)
            }
        }

        saveToPropertyFile(settingsAsPropMap)

        registerPossibleUnresolvedValues(settingsAsPropMap)

        return this.settings.values.toList()
    }
}