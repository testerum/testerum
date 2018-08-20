package com.testerum.web_backend.controller.step

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.service.step.StepCache
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/basic")
open class BasicStepsController(val stepCache: StepCache) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun getBasicSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): List<BasicStepDef> {
        return stepCache.getBasicSteps(stepsTreeFilter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getBasicStepByPath(@RequestParam(value = "path") path: String): BasicStepDef? {
        return stepCache.getBasicStepByPath(Path.createInstance(path))
    }

}
