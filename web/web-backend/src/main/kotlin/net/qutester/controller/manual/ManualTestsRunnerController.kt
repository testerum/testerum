package net.qutester.controller.manual

import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.runner.ManualTestsRunner
import net.qutester.model.manual.runner.operation.UpdateManualTestExecutionModel
import net.qutester.model.manual.runner.operation.UpdateManualTestsRunnerModel
import net.qutester.service.manual.ManualTestsRunnerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manualTestsRunner")
class ManualTestsRunnerController(private val manualTestsRunnerService: ManualTestsRunnerService) {

    @RequestMapping (path = ["/create"], method = [RequestMethod.POST])
    @ResponseBody
    fun create(@RequestBody manualTestsRunner: ManualTestsRunner): ManualTestsRunner {
        return manualTestsRunnerService.createTest(manualTestsRunner)
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getTestsRunnerAtPath(@RequestParam(value = "path") path:String): ManualTestsRunner? {
        return manualTestsRunnerService.getTestsRunnerAtPath(Path.createInstance(path))
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.DELETE])
    fun delete(@RequestParam(value = "path") path:String) {
        manualTestsRunnerService.remove(Path.createInstance(path))
    }

    @RequestMapping (path = ["/finalize"], params = ["path"], method = [RequestMethod.POST])
    fun finalize(@RequestParam(value = "path") path:String): ManualTestsRunner {
        return manualTestsRunnerService.finalize(Path.createInstance(path))
    }

    @RequestMapping (path = ["/bringBackInExecution"], params = ["path"], method = [RequestMethod.POST])
    fun bringBackInExecution(@RequestParam(value = "path") path:String): ManualTestsRunner {
        return manualTestsRunnerService.bringBackInExecution(Path.createInstance(path))
    }

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getTests(): List<ManualTestsRunner> {
        return manualTestsRunnerService.getAllTestsRunners()
    }

    @RequestMapping (path = ["/update"], method = [RequestMethod.POST])
    @ResponseBody
    fun update(@RequestBody updateManualTestsRunnerModel: UpdateManualTestsRunnerModel): ManualTestsRunner {
        return manualTestsRunnerService.updateTest(updateManualTestsRunnerModel)
    }

    @RequestMapping (path = ["/updateTestExecution"], method = [RequestMethod.POST])
    @ResponseBody
    fun updateTestExecution(@RequestBody updateManualTestExecutionModel: UpdateManualTestExecutionModel): ManualTestsRunner? {
        return manualTestsRunnerService.updateTestExecution(updateManualTestExecutionModel)
    }
}