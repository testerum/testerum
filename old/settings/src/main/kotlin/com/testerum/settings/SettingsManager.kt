package com.testerum.settings

import com.testerum.settings.reference_resolver.SettingsResolver
import com.testerum_api.testerum_steps_api.test_context.settings.model.Setting
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class SettingsManager {

    private val lock = ReentrantReadWriteLock()

    private var definitions = LinkedHashMap<String, SettingDefinition>()
    private var values = LinkedHashMap<String, String>()

    private var resolved = LinkedHashMap<String, Setting>()

    fun modify(build: SettingsManagerModifier.() -> Unit) {
        lock.write {
            val additionalDefinitions = LinkedHashMap<String, SettingDefinition>()
            val additionalValues = LinkedHashMap<String, String>()

            val modifier = object : SettingsManagerModifier {
                override fun registerDefinition(definition: SettingDefinition) {
                    additionalDefinitions[definition.key] = definition
                }

                override fun registerDefinitions(definitions: List<SettingDefinition>) {
                    for (definition in definitions) {
                        registerDefinition(definition)
                    }
                }

                override fun setValue(key: String, value: String) {
                    additionalValues[key] = value
                }

                override fun setValues(settingValues: Map<String, String>) {
                    for ((key, value) in settingValues) {
                        setValue(key, value)
                    }
                }
            }

            modifier.build()

            resolveWith(additionalDefinitions, additionalValues)
        }
    }

    fun getSettings(): List<Setting> {
        return lock.read {
            resolved.values.toList()
        }
    }

    fun getSetting(key: String): Setting? {
        return lock.read {
            resolved[key]
        }
    }

    private fun resolveWith(additionalDefinitions: LinkedHashMap<String, SettingDefinition>,
                            additionalValues: LinkedHashMap<String, String>) {
        val newDefinitions = LinkedHashMap<String, SettingDefinition>().apply {
            putAll(definitions)
            putAll(additionalDefinitions)
        }
        val newValues = LinkedHashMap<String, String>().apply {
            putAll(this@SettingsManager.values)
            putAll(additionalValues)
        }

        val resolvedValues: Map<String, String> = resolveValues(newDefinitions, newValues)
        val newResolved = resolveSettings(newDefinitions, newValues, resolvedValues)

        definitions = newDefinitions
        values = newValues
        resolved = newResolved
    }

    private fun resolveValues(definitions: Map<String, SettingDefinition>,
                              values: Map<String, String>): Map<String, String> {
        val unresolvedValues = LinkedHashMap<String, String>()

        // default values
        for ((key, definition) in definitions) {
            unresolvedValues[key] = definition.defaultValue
        }

        // actual value overrides
        for ((key, value) in values) {
            unresolvedValues[key] = value
        }

        return SettingsResolver.resolve(unresolvedValues)
    }

    private fun resolveSettings(definitions: Map<String, SettingDefinition>,
                                values: Map<String, String>,
                                resolvedValues: Map<String, String>): LinkedHashMap<String, Setting> {
        val result = LinkedHashMap<String, Setting>()

        for ((key, resolvedValue) in resolvedValues) {
            val definition = definitions[key]
                    ?: SettingDefinition.undefined(key)
            val value = values[key]
                    ?: definition.defaultValue

            result[key] = Setting(definition, value, resolvedValue)
        }

        return result
    }
}
