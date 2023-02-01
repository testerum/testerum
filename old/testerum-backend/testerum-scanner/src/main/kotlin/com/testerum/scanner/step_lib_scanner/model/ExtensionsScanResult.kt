package com.testerum.scanner.step_lib_scanner.model

import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition

data class ExtensionsScanResult(
    val steps: List<BasicStepDef>,
    val hooks: List<HookDef>,
    val settingDefinitions: List<SettingDefinition>
)
