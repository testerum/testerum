package com.testerum.web_backend.controllers.steps

import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.tree.RootStepNode
import com.testerum.web_backend.services.steps.StepsTreeFrontendService
import org.springframework.web.bind.annotation.*

@RestController
class StepsTreeController(private val stepsTreeFrontendService: StepsTreeFrontendService) {

    @RequestMapping(method = [RequestMethod.POST], path = ["/steps/tree"])
    @ResponseBody
    fun getStepsTree(@RequestBody filter: StepsTreeFilter): RootStepNode {
        return stepsTreeFrontendService.getStepTree(filter)
    }

}
