package com.testerum.web_backend.controllers.results

import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.web_backend.services.runner.result.ResultsFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/results")
class ResultsController(private val resultsFrontendService: ResultsFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getResults(): List<RunnerResultsDirInfo> {
        return resultsFrontendService.getResults()
    }

}
