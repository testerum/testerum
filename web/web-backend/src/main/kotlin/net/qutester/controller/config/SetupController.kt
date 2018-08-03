package net.qutester.controller.config

import com.testerum.settings.private_api.SettingsManagerImpl
import net.qutester.model.config.Setup
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/setup")
open class SetupController(val settingsManager: SettingsManagerImpl,
                           val stepService: StepService) {

    @RequestMapping(path = ["/is_completed"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun isSetupCompleted(): Boolean {
        return settingsManager.isConfigSet()
    }

    @RequestMapping (method = [(RequestMethod.PUT)])
    @ResponseBody
    fun createConfig(@RequestBody setup: Setup): Setup {
        settingsManager.createConfig (
                setup.repositoryPath.toJavaPath()
        )
        stepService.reinitializeComposedSteps()

        return setup
    }
}
