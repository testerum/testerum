package com.testerum.web_backend.controller.step

import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.tree.RootStepNode
import com.testerum.service.step.StepService
import org.springframework.web.bind.annotation.*

@RestController
class StepsTreeController(val stepService: StepService) {

    @RequestMapping(method = [RequestMethod.POST], path = ["/steps/tree"])
    @ResponseBody
    fun getStepsTree(@RequestBody stepsTreeFilter: StepsTreeFilter): RootStepNode {
        return stepService.getStepTree(stepsTreeFilter)
    }

}