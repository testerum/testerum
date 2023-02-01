package com.testerum.settings

import com.testerum_api.testerum_steps_api.test_context.settings.model.Setting

fun SettingsManager.getNonDefaultSettings(): List<Setting> {
    return getSettings().filter {
        it.unresolvedValue != it.definition.defaultValue
    }
}

fun SettingsManager.getResolvedSettingValues(): Map<String, String> {
    return getSettings().associateBy(
            { it.definition.key },
            { it.resolvedValue }
    )
}

fun SettingsManager.getRequiredSetting(key: String): Setting {
    return getSetting(key)
            ?: throw IllegalArgumentException("missing required setting [$key]")
}

fun SettingsManager.hasValue(key: String): Boolean {
    val setting = getSetting(key)
            ?: return false

    return setting.resolvedValue.isNotBlank()
}

fun SettingsManager.getValue(key: String): String? {
    return getSetting(key)?.resolvedValue
}
