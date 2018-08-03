package net.qutester.controller.config

import net.qutester.controller.config.model.UiSetting
import net.qutester.controller.config.model.toUiSetting
import net.qutester.service.config.SettingsService
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/settings")
open class SettingsController(val settingsService: SettingsService,
                              val stepService: StepService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getSettings(): List<UiSetting> {
        return settingsService.getSettings().map { it.toUiSetting() }
    }

    @RequestMapping(method = [RequestMethod.POST])
    @ResponseBody
    fun saveSettings(@RequestBody uiSettings: List<UiSetting>): List<UiSetting> {
        val settingWithValues = uiSettings.map { it.toSettingWithValue() }
        val settingsAfterSave = settingsService.save(settingWithValues)
        stepService.loadSteps()

        return settingsAfterSave.map { it.toUiSetting() }
    }
}