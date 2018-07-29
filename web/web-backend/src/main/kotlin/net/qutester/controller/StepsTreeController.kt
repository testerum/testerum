package net.qutester.controller

import net.qutester.model.step.filter.StepsTreeFilter
import net.qutester.model.step.tree.RootStepNode
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
class StepsTreeController(val stepService: StepService) {

    @RequestMapping("/steps/tree", method = [RequestMethod.POST])
    @ResponseBody
    fun getStepsTree(@RequestBody stepsTreeFilter: StepsTreeFilter): RootStepNode {
        return stepService.getStepTree(stepsTreeFilter)
    }

}