package com.testerum_api.testerum_steps_api.test_context.settings.model

data class SettingDefinition(val key: String,
                             val label: String,
                             val type: SettingType,
                             val defaultValue: String,
                             val enumValues: List<String> = emptyList(),
                             val description: String? = null,
                             val category: String? = null,
                             val defined: Boolean = true) {
    companion object {
        fun undefined(key: String) = SettingDefinition(
                key = key,
                label = key,
                type = SettingType.TEXT,
                defaultValue = "",
                description = null,
                category = null,
                defined = false
        )
    }

}
