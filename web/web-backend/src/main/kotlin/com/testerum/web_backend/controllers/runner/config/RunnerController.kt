package com.testerum.web_backend.controllers.runner.config

import com.testerum.model.runner.config.RunnerConfig
import com.testerum.web_backend.services.runner.config.RunnerConfigFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/runner")
open class RunnerController(private val runnerConfigFrontendService: RunnerConfigFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getRunnerConfigs(): List<RunnerConfig> {
        return runnerConfigFrontendService.getRunnerConfigs()
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun saveRunnerConfig(@RequestBody runnerConfig: RunnerConfig): RunnerConfig {
        return runnerConfigFrontendService.saveRunnerConfig(runnerConfig)
    }

}
