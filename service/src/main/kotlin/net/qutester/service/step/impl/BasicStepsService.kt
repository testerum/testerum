package net.qutester.service.step.impl

import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.settings.SystemSettings
import net.qutester.model.step.BasicStepDef
import net.qutester.service.scanner.ScannerService

open class BasicStepsService(val scannerService: ScannerService,
                             val settingsManager: SettingsManager) {

    private var basicSteps: List<BasicStepDef> = emptyList()
    private var scannedBasicStepDirectory = settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)

    fun init() {
        basicSteps = scannerService.getBasicSteps()
    }

    fun getBasicSteps():List<BasicStepDef> {
        if (basicSteps.isEmpty() || scannedBasicStepDirectory != settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)) {
            scannedBasicStepDirectory = settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)
            init()
        }

        return basicSteps
    }

    fun getStepsPackages(): Set<String> {
        val basicSteps: List<BasicStepDef> = getBasicSteps()
        return basicSteps.map { it.path }
                .map { it.directories.joinToString (".") }
                .toSet()
    }
}
