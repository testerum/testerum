package net.qutester.controller.report

import com.testerum.runner.events.model.RunnerEvent
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.run_result.RunnerResultsDirInfo
import net.qutester.service.tests_runner.TestRunnerResultService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/runResults")
class RunnerResultController(private val testRunnerResultService: TestRunnerResultService) {

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getResults(): List<RunnerResultsDirInfo> {
        return testRunnerResultService.getResultInfo()
    }


    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getResultAtPath(@RequestParam(value = "path") path:String): List<RunnerEvent>? {
        return testRunnerResultService.getResultAtPath(Path.createInstance(path))
    }

}