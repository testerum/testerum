package com.testerum.web_backend.controller.manual

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.runner.ManualTestsRunner
import com.testerum.model.manual.runner.operation.UpdateManualTestExecutionModel
import com.testerum.model.manual.runner.operation.UpdateManualTestsRunnerModel
import com.testerum.service.manual.ManualTestsRunnerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manualTestsRunner")
class ManualTestsRunnerController(private val manualTestsRunnerService: ManualTestsRunnerService) {

    @RequestMapping (method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getTests(): List<ManualTestsRunner> {
        return manualTestsRunnerService.getAllTestsRunners()
    }

    @RequestMapping (method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestsRunnerAtPath(@RequestParam(value = "path") path:String): ManualTestsRunner? {
        return manualTestsRunnerService.getTestsRunnerAtPath(Path.createInstance(path))
    }

    @RequestMapping (method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path:String) {
        manualTestsRunnerService.remove(Path.createInstance(path))
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/create"])
    @ResponseBody
    fun create(@RequestBody manualTestsRunner: ManualTestsRunner): ManualTestsRunner {
        return manualTestsRunnerService.createTest(manualTestsRunner)
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/finalize"], params = ["path"])
    fun finalize(@RequestParam(value = "path") path:String): ManualTestsRunner {
        return manualTestsRunnerService.finalize(Path.createInstance(path))
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/bringBackInExecution"], params = ["path"])
    fun bringBackInExecution(@RequestParam(value = "path") path:String): ManualTestsRunner {
        return manualTestsRunnerService.bringBackInExecution(Path.createInstance(path))
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/update"])
    @ResponseBody
    fun update(@RequestBody updateManualTestsRunnerModel: UpdateManualTestsRunnerModel): ManualTestsRunner {
        return manualTestsRunnerService.updateTest(updateManualTestsRunnerModel)
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/updateTestExecution"])
    @ResponseBody
    fun updateTestExecution(@RequestBody updateManualTestExecutionModel: UpdateManualTestExecutionModel): ManualTestsRunner? {
        return manualTestsRunnerService.updateTestExecution(updateManualTestExecutionModel)
    }

}