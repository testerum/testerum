package com.testerum.web_backend.controller.config

import com.testerum.service.config.SettingsService
import com.testerum.service.step.StepCache
import com.testerum.web_backend.controller.config.model.UiSetting
import com.testerum.web_backend.controller.config.model.toUiSetting
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/settings")
open class SettingsController(val settingsService: SettingsService,
                              val stepCache: StepCache) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getSettings(): List<UiSetting> {
        return settingsService.getSettings().map { it.toUiSetting() }
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun saveSettings(@RequestBody uiSettings: List<UiSetting>): List<UiSetting> {
        val settingWithValues = uiSettings.map { it.toSettingWithValue() }
        val settingsAfterSave = settingsService.save(settingWithValues)
        stepCache.loadSteps()

        return settingsAfterSave.map { it.toUiSetting() }
    }

}
