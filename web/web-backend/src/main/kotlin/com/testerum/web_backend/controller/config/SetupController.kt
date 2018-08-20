package com.testerum.web_backend.controller.config

import com.testerum.model.config.Setup
import com.testerum.service.step.StepCache
import com.testerum.settings.private_api.SettingsManagerImpl
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/setup")
open class SetupController(val settingsManager: SettingsManagerImpl,
                           val stepCache: StepCache) {

    @RequestMapping (method = [RequestMethod.PUT], path = [""])
    @ResponseBody
    fun createConfig(@RequestBody setup: Setup): Setup {
        settingsManager.createConfig (
                setup.repositoryPath.toJavaAbsolutePath()
        )
        stepCache.reinitializeComposedSteps()

        return setup
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/is_completed"])
    @ResponseBody
    fun isSetupCompleted(): Boolean {
        return settingsManager.isConfigSet()
    }

}
