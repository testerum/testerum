package com.testerum.scanner.step_lib_scanner.model

import com.testerum.api.test_context.settings.model.SettingDefinition
import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef

data class ExtensionsScanResult(val steps: List<BasicStepDef>,
                                val hooks: List<HookDef>,
                                val settingDefinitions: List<SettingDefinition>)
