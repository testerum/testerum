package com.testerum.service.step.impl

import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.model.step.BasicStepDef
import com.testerum.service.scanner.ScannerService
import com.testerum.settings.SettingsManager
import com.testerum.settings.keys.SystemSettingKeys

open class BasicStepsService(val scannerService: ScannerService,
                             val settingsManager: SettingsManager) {

    private var basicSteps: List<BasicStepDef> = emptyList()
    private var scannedBasicStepDirectory = settingsManager.getSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR)?.resolvedValueAsPath

    private fun init() {
        basicSteps = scannerService.getBasicSteps()
    }

    fun getBasicSteps():List<BasicStepDef> {
        if (basicSteps.isEmpty() || scannedBasicStepDirectory != settingsManager.getSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR)?.resolvedValueAsPath) {
            scannedBasicStepDirectory = settingsManager.getSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR)?.resolvedValueAsPath
            init()
        }

        return basicSteps
    }

}
