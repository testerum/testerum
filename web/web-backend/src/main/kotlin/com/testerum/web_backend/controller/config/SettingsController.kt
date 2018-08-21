package com.testerum.web_backend.controller.config

import com.testerum.api.test_context.settings.model.Setting
import com.testerum.service.settings.SettingsService
import com.testerum.service.step.StepCache
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/settings")
open class SettingsController(val settingsService: SettingsService,
                              val stepCache: StepCache) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getSettings(): List<Setting> {
        return settingsService.getSettings()
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun saveSettings(@RequestBody settings: Map<String, String>): List<Setting> {
        val settingsAfterSave = settingsService.save(settings)

        stepCache.loadSteps()

        return settingsAfterSave
    }

}
