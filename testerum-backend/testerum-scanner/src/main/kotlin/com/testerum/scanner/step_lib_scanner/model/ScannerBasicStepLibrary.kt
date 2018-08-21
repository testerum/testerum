package com.testerum.scanner.step_lib_scanner.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.testerum.api.test_context.settings.model.SettingDefinition
import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef

data class ScannerBasicStepLibrary(val jarFile: ScannerLibraryFile,
                                   val steps: List<BasicStepDef>,
                                   val hooks: List<HookDef>,
                                   val settingDefinitions: List<SettingDefinition>) {

    @JsonIgnore
    fun isNotEmpty() = steps.isNotEmpty() || hooks.isNotEmpty() || settingDefinitions.isNotEmpty()

}
