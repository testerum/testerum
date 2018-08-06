package com.testerum.web_backend.controller.runner

import com.testerum.service.tests_runner.execution.TestsExecutionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests/executions")
class TestExecutionController(private val testsExecutionService: TestsExecutionService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun createExecution(): Long {
        return testsExecutionService.createExecution()
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["{executionId}"])
    @ResponseBody
    fun stopExecution(@PathVariable executionId: Long) {
        testsExecutionService.stopExecution(executionId)
    }

}