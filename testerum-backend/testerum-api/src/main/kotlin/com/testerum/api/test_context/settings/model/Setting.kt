package com.testerum.api.test_context.settings.model

data class Setting(val definition: SettingDefinition,
                   val unresolvedValue: String,
                   val resolvedValue: String)
