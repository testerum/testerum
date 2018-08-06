package net.qutester.controller.report

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.events.model.RunnerEvent
import net.qutester.service.tests_runner.result.TestRunnerResultService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/runResults")
class RunnerResultController(private val testRunnerResultService: TestRunnerResultService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getResults(): List<RunnerResultsDirInfo> {
        return testRunnerResultService.getResultInfo()
    }


    @RequestMapping(method = [RequestMethod.GET], params = ["path"])
    @ResponseBody
    fun getResultAtPath(@RequestParam(value = "path") path: String): List<RunnerEvent>? {
        return testRunnerResultService.getResultAtPath(Path.createInstance(path))
    }

}