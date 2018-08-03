package net.qutester.controller.manual

import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.runner.ManualTestsRunner
import net.qutester.model.manual.runner.operation.UpdateManualTestExecutionModel
import net.qutester.model.manual.runner.operation.UpdateManualTestsRunnerModel
import net.qutester.service.manual.ManualTestsRunnerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manualTestsRunner")
class ManualTestsRunnerController(private val manualTestsRunnerService: ManualTestsRunnerService) {
    private val LOG = LoggerFactory.getLogger(ManualTestsRunnerController::class.java)

    @RequestMapping (path = arrayOf("/create"), method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun create(@RequestBody manualTestsRunner: ManualTestsRunner): ManualTestsRunner {
        return manualTestsRunnerService.createTest(manualTestsRunner)
    }

    @RequestMapping (params = arrayOf("path"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getTestsRunnerAtPath(@RequestParam(value = "path") path:String): ManualTestsRunner? {
        return manualTestsRunnerService.getTestsRunnerAtPath(Path.createInstance(path))
    }

    @RequestMapping (params = arrayOf("path"), method = arrayOf(RequestMethod.DELETE))
    fun delete(@RequestParam(value = "path") path:String) {
        manualTestsRunnerService.remove(Path.createInstance(path))
    }

    @RequestMapping (path = arrayOf("/finalize"), params = arrayOf("path"), method = arrayOf(RequestMethod.POST))
    fun finalize(@RequestParam(value = "path") path:String): ManualTestsRunner {
        return manualTestsRunnerService.finalize(Path.createInstance(path))
    }

    @RequestMapping (path = arrayOf("/bringBackInExecution"), params = arrayOf("path"), method = arrayOf(RequestMethod.POST))
    fun bringBackInExecution(@RequestParam(value = "path") path:String): ManualTestsRunner {
        return manualTestsRunnerService.bringBackInExecution(Path.createInstance(path))
    }

    @RequestMapping (method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getTests(): List<ManualTestsRunner> {
        return manualTestsRunnerService.getAllTestsRunners()
    }

    @RequestMapping (path = arrayOf("/update"), method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun update(@RequestBody updateManualTestsRunnerModel: UpdateManualTestsRunnerModel): ManualTestsRunner {
        return manualTestsRunnerService.updateTest(updateManualTestsRunnerModel)
    }

    @RequestMapping (path = arrayOf("/updateTestExecution"), method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun updateTestExecution(@RequestBody updateManualTestExecutionModel: UpdateManualTestExecutionModel): ManualTestsRunner? {
        return manualTestsRunnerService.updateTestExecution(updateManualTestExecutionModel)
    }
}