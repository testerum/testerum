package com.testerum.api.test_context.settings.model

data class SettingDefinition(val key: String,
                             val type: SettingType,
                             val defaultValue: String,
                             val description: String? = null,
                             val category: String? = null,
                             val defined: Boolean = true) {
    companion object {
        fun undefined(key: String) = SettingDefinition(
                key = key,
                type = SettingType.TEXT,
                defaultValue = "",
                description = null,
                category = null,
                defined = false
        )
    }

}
