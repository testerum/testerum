package com.testerum.web_backend.controllers.runner.execution

import com.testerum.model.runner.config.RunConfig
import com.testerum.web_backend.services.runner.execution.TestsExecutionFrontendService
import com.testerum.web_backend.services.runner.execution.model.TestExecutionResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tests/executions")
class TestExecutionController(private val testsExecutionFrontendService: TestsExecutionFrontendService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun createExecution(@RequestBody runConfig: RunConfig): TestExecutionResponse {
        return testsExecutionFrontendService.createExecution(runConfig)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["{executionId}"])
    @ResponseBody
    fun stopExecution(@PathVariable executionId: Long) {
        testsExecutionFrontendService.stopExecution(executionId)
    }

}
