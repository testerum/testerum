package com.testerum.web_backend.controllers.runner.config

import com.testerum.model.runner.config.RunnerConfig
import com.testerum.web_backend.services.runner.config.RunnerConfigFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/runner")
open class RunConfigController(private val runnerConfigFrontendService: RunnerConfigFrontendService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getRunConfigs(): List<RunnerConfig> {
        return runnerConfigFrontendService.getRunConfigs()
    }

    @RequestMapping(method = [RequestMethod.POST])
    @ResponseBody
    fun saveRunnerConfig(@RequestBody runnerConfigs: List<RunnerConfig>): List<RunnerConfig> {
        return runnerConfigFrontendService.saveRunnerConfig(runnerConfigs)
    }

}
