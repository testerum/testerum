package com.testerum.web_backend.controllers.variables

import com.testerum.model.variable.AllProjectVariables
import com.testerum.web_backend.services.variables.VariablesFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/variables")
class VariablesController(private val variablesFrontendService: VariablesFrontendService) {

    @RequestMapping (method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getAllProjectVariables(): AllProjectVariables {
        return variablesFrontendService.getAllProjectVariables()
    }

    @RequestMapping (method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun saveAllProjectVariables(@RequestBody allProjectVariables: AllProjectVariables): AllProjectVariables {
        return variablesFrontendService.saveAllProjectVariables(allProjectVariables)
    }

    @RequestMapping (method = [RequestMethod.PUT], path = ["/environment"], params = ["currentEnvironment"])
    @ResponseBody
    fun saveCurrentEnvironment(@RequestParam(value = "currentEnvironment") currentEnvironment: String): String {
        //TODO Cristi: implement this
        return currentEnvironment
    }
}
