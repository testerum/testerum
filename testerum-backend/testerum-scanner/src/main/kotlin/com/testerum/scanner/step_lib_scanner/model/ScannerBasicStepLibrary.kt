package com.testerum.scanner.step_lib_scanner.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.api.test_context.settings.model.Setting
import net.qutester.model.step.BasicStepDef

data class ScannerBasicStepLibrary(val jarFile: ScannerLibraryFile,
                                   val steps: List<BasicStepDef>,
                                   val hooks: List<HookDef>,
                                   val settings: List<Setting>) {

    @JsonIgnore
    fun isNotEmpty() = steps.isNotEmpty() || hooks.isNotEmpty() || settings.isNotEmpty()

}
