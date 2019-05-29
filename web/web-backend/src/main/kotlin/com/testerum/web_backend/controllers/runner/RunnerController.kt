package com.testerum.web_backend.controllers.runner

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.RunnerConfig
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/runner")
open class RunnerController() {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getRunnerConfigs(): List<RunnerConfig> {
        return listOf(
                RunnerConfig(
                        "user",
                        mapOf(Pair("testerum.http.socketTimeoutMillis", "250")),
                        listOf("rest"),
                        listOf("selenium"),
                        listOf(
                                Path.createInstance("ui/owners"),
                                Path.createInstance("restful ap/specialities/rest")
                        )
                )
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun addRunnerConfigs(@RequestBody runnerConfig: List<RunnerConfig>): List<RunnerConfig> {
        println("runnerConfig = ${runnerConfig}")
        return runnerConfig;
    }
}
