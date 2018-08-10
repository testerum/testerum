package com.testerum.web_backend.controller.runner

import com.testerum.model.infrastructure.path.Path
import com.testerum.service.tests_runner.execution.TestsExecutionService
import com.testerum.service.tests_runner.execution.model.TestExecutionResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests/executions")
class TestExecutionController(private val testsExecutionService: TestsExecutionService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun createExecution(@RequestBody pathsToRun: List<String>): TestExecutionResponse {
        return testsExecutionService.createExecution(
                pathsToRun = pathsToRun.map { Path.createInstance(it) }
        )
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["{executionId}"])
    @ResponseBody
    fun stopExecution(@PathVariable executionId: Long) {
        testsExecutionService.stopExecution(executionId)
    }

}
