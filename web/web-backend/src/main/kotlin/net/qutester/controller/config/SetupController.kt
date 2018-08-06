package net.qutester.controller.config

import com.testerum.model.config.Setup
import com.testerum.service.step.StepService
import com.testerum.settings.private_api.SettingsManagerImpl
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/setup")
open class SetupController(val settingsManager: SettingsManagerImpl,
                           val stepService: StepService) {

    @RequestMapping (method = [RequestMethod.PUT], path = [""])
    @ResponseBody
    fun createConfig(@RequestBody setup: Setup): Setup {
        settingsManager.createConfig (
                setup.repositoryPath.toJavaPath()
        )
        stepService.reinitializeComposedSteps()

        return setup
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/is_completed"])
    @ResponseBody
    fun isSetupCompleted(): Boolean {
        return settingsManager.isConfigSet()
    }

}
