package com.testerum.web_backend.controllers.variables

import com.testerum.model.variable.AllProjectVariables
import com.testerum.model.variable.Variable
import com.testerum.model.variable.VariablesEnvironment
import com.testerum.web_backend.services.variables.VariablesFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

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
    fun save(@RequestBody allProjectVariables: AllProjectVariables): AllProjectVariables {
//        return variablesFrontendService.save(variables)
        return allProjectVariables
    }

    @RequestMapping (method = [RequestMethod.PUT], path = ["/environment"])
    @ResponseBody
    fun saveCurrentEnvironment(@RequestBody currentEnvironment: String): String {
        return currentEnvironment
    }

}
