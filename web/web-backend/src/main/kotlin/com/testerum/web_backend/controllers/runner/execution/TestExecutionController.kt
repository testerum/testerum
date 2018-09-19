package com.testerum.web_backend.controllers.runner.execution

import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.runner.execution.TestsExecutionFrontendService
import com.testerum.web_backend.services.runner.execution.model.TestExecutionResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests/executions")
class TestExecutionController(private val testsExecutionFrontendService: TestsExecutionFrontendService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun createExecution(@RequestBody pathsToRun: List<String>): TestExecutionResponse {
        return testsExecutionFrontendService.createExecution(
                testOrDirectoryPaths = pathsToRun.map { Path.createInstance(it) }
        )
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["{executionId}"])
    @ResponseBody
    fun stopExecution(@PathVariable executionId: Long) {
        testsExecutionFrontendService.stopExecution(executionId)
    }

}
