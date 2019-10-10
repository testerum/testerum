package com.testerum.scanner.step_lib_scanner.impl

import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSetting
import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSettings
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import io.github.classgraph.ClassInfo
import java.util.Collections

val SETTINGS_CLASS_ANNOTATIONS = setOf<String>(
        DeclareSettings::class.java.name,
        DeclareSetting::class.java.name
)

private fun ClassInfo.hasSettingDefinitions(): Boolean {
    for (settingClassAnnotation in SETTINGS_CLASS_ANNOTATIONS) {
        if (this.hasAnnotation(settingClassAnnotation)) {
            return true
        }
    }

    return false
}

fun ClassInfo.getSettingDefinitions(): List<SettingDefinition> {
    if (!this.hasSettingDefinitions()) {
        return Collections.emptyList()
    }

    val result = mutableListOf<SettingDefinition>()

    for (annotationInfo in this.getAnnotationInfoRepeatable(DeclareSetting::class.java.name)) {
        val declareSetting = annotationInfo.loadClassAndInstantiate() as DeclareSetting

        result += declareSetting.toSetting()
    }

    return result
}

private fun DeclareSetting.toSetting() = SettingDefinition(key, label, type, defaultValue, enumValues.toList(), description, category)
