package net.qutester.controller.step

import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.BasicStepDef
import net.qutester.model.step.filter.StepsTreeFilter
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/basic")
open class BasicStepsController(val stepService: StepService) {

    @RequestMapping (method = [RequestMethod.POST])
    @ResponseBody
    fun getBasicSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): List<BasicStepDef> {
        return stepService.getBasicSteps(stepsTreeFilter)
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getBasicStepByPath(@RequestParam(value = "path") path:String): BasicStepDef? {
        return stepService.getBasicStepByPath(Path.createInstance(path))
    }

}