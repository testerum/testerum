package net.qutester.controller

import net.qutester.model.step.filter.StepsTreeFilter
import net.qutester.model.step_tree.RootStepNode
import net.qutester.service.step.StepService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class StepsTreeController(val stepService: StepService) {

    @RequestMapping("/steps/tree", method = [RequestMethod.POST])
    fun getBasicSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): RootStepNode {
        return stepService.getStepTree(stepsTreeFilter)
    }

}