package com.testerum_api.testerum_steps_api.test_context.settings.model

data class Setting(val definition: SettingDefinition,
                   val unresolvedValue: String,
                   val resolvedValue: String)
