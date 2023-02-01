package com.testerum.settings

import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition

interface SettingsManagerModifier {

    fun registerDefinition(definition: SettingDefinition)
    fun registerDefinitions(definitions: List<SettingDefinition>)

    fun setValue(key: String, value: String)
    fun setValues(settingValues: Map<String, String>)

}
