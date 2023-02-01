package com.testerum.web_backend.controllers.runner.config

import com.testerum.model.runner.config.RunConfig
import com.testerum.web_backend.services.runner.config.RunConfigFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/run-configs")
open class RunConfigController(private val runConfigFrontendService: RunConfigFrontendService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getRunConfigs(): List<RunConfig> {
        return runConfigFrontendService.getRunConfigs()
    }

    @RequestMapping(method = [RequestMethod.POST])
    @ResponseBody
    fun saveRunConfig(@RequestBody runConfigs: List<RunConfig>): List<RunConfig> {
        return runConfigFrontendService.saveRunConfig(runConfigs)
    }

}
