package com.testerum.web_backend.controllers.runner.result

import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.web_backend.services.runner.result.RunnerResultFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/runner-reports")
class RunnerResultController(private val runnerResultFrontendService: RunnerResultFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getResults(): List<RunnerResultsDirInfo> {
        return runnerResultFrontendService.getResults()
    }

}
