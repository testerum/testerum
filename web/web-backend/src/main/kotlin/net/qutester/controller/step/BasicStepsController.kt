package net.qutester.controller.step

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/basic")
open class BasicStepsController(val stepService: StepService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun getBasicSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): List<BasicStepDef> {
        return stepService.getBasicSteps(stepsTreeFilter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getBasicStepByPath(@RequestParam(value = "path") path: String): BasicStepDef? {
        return stepService.getBasicStepByPath(Path.createInstance(path))
    }

}