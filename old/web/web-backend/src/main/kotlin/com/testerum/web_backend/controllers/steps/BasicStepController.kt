package com.testerum.web_backend.controllers.steps

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.web_backend.services.steps.BasicStepsFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/basic")
class BasicStepController(private val basicStepsFrontendService: BasicStepsFrontendService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun getBasicSteps(@RequestBody filter: StepsTreeFilter): List<BasicStepDef> {
        return basicStepsFrontendService.getBasicSteps(filter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getBasicStepAtPath(@RequestParam(value = "path") path: String): BasicStepDef? {
        return basicStepsFrontendService.getBasicStepAtPath(
                Path.createInstance(path)
        )
    }

}
