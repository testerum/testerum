package com.testerum.web_backend.controllers.runner.result

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.web_backend.services.runner.result.RunnerResultFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/runResults")
class RunnerResultController(private val runnerResultFrontendService: RunnerResultFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getResults(): List<RunnerResultsDirInfo> {
        return runnerResultFrontendService.getResults()
    }


    @RequestMapping(method = [RequestMethod.GET], params = ["path"])
    @ResponseBody
    fun getResultAtPath(@RequestParam(value = "path") path: String): List<RunnerEvent>? {
        return runnerResultFrontendService.getResultAtPath(
                Path.createInstance(path)
        )
    }

}
