package com.testerum.web_backend.controllers.setup

import com.testerum.model.config.Setup
import com.testerum.web_backend.services.setup.SetupFrontendService
import org.springframework.web.bind.annotation.*
import java.nio.file.Paths

@RestController
@RequestMapping("/setup")
open class SetupController(private val setupFrontendService: SetupFrontendService) {

    @RequestMapping (method = [RequestMethod.PUT], path = [""])
    @ResponseBody
    fun createConfig(@RequestBody setup: Setup): Setup {
        setupFrontendService.createConfig(
                Paths.get(setup.repositoryAbsoluteJavaPath)
        )

        return setup
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/is_completed"])
    @ResponseBody
    fun isSetupCompleted(): Boolean = setupFrontendService.isSetupCompleted()

}
