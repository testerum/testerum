package net.qutester.controller.report

import com.testerum.runner.events.model.RunnerEvent
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.run_result.RunnerResultsDirInfo
import net.qutester.service.tests_runner.TestRunnerResultService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/runResults")
class RunnerResultController(private val testRunnerResultService: TestRunnerResultService) {
    private val LOG = LoggerFactory.getLogger(RunnerResultController::class.java)

    @RequestMapping (method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getResults(): List<RunnerResultsDirInfo> {
        return testRunnerResultService.getResultInfo()
    }


    @RequestMapping (params = arrayOf("path"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getResultAtPath(@RequestParam(value = "path") path:String): List<RunnerEvent>? {
        return testRunnerResultService.getResultAtPath(Path.createInstance(path))
    }

}