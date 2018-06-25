package net.qutester.controller

import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.BasicStepDef
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/basic")
open class BasicStepsController(val stepService: StepService) {

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getBasicSteps(): List<BasicStepDef> {
        return stepService.getBasicSteps()
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBasicStepByPath(@RequestParam(value = "path") path:String): BasicStepDef? {
        return stepService.getBasicStepByPath(Path.createInstance(path));
    }

}