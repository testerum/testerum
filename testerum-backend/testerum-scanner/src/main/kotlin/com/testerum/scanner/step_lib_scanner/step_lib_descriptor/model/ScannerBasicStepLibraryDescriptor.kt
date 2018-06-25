package com.testerum.scanner.step_lib_scanner.step_lib_descriptor.model

import com.testerum.api.test_context.settings.model.Setting

data class ScannerBasicStepLibraryDescriptor(val name: String,
                                             val description: String? = null,
                                             val settings: List<Setting> = emptyList()) {

    init {
        validateSettings()
    }

    private fun validateSettings() {
        val uniqueKeys = hashSetOf<String>()

        for (setting in settings) {
            val added = uniqueKeys.add(setting.key)

            if (!added) {
                throw IllegalArgumentException("error: found duplicate setting key [${setting.key}]")
            }
        }
    }

}
