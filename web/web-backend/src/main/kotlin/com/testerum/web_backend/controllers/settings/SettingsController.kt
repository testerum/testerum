package com.testerum.web_backend.controllers.settings

import com.testerum.api.test_context.settings.model.Setting
import com.testerum.web_backend.services.settings.SettingsFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/settings")
open class SettingsController(private val settingsFrontendService: SettingsFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getSettings(): List<Setting> {
        return settingsFrontendService.getSettings()
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun saveSettings(@RequestBody settings: Map<String, String>): List<Setting> {
        return settingsFrontendService.saveSettings(settings)
    }

}
