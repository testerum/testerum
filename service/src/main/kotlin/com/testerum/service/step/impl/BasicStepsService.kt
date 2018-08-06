package com.testerum.service.step.impl

import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.model.step.BasicStepDef
import com.testerum.service.scanner.ScannerService
import com.testerum.settings.SystemSettings

open class BasicStepsService(val scannerService: ScannerService,
                             val settingsManager: SettingsManager) {

    private var basicSteps: List<BasicStepDef> = emptyList()
    private var scannedBasicStepDirectory = settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)

    private fun init() {
        basicSteps = scannerService.getBasicSteps()
    }

    fun getBasicSteps():List<BasicStepDef> {
        if (basicSteps.isEmpty() || scannedBasicStepDirectory != settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)) {
            scannedBasicStepDirectory = settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)
            init()
        }

        return basicSteps
    }

}
