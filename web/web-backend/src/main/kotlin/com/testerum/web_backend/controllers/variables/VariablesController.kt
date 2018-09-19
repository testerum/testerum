package com.testerum.web_backend.controllers.variables

import com.testerum.model.variable.Variable
import com.testerum.web_backend.services.variables.VariablesFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/variables")
class VariablesController(private val variablesFrontendService: VariablesFrontendService) {

    @RequestMapping (method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getVariables(): List<Variable> {
        return variablesFrontendService.getVariables()
    }

    @RequestMapping (method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun save(@RequestBody variables: List<Variable>): List<Variable> {
        return variablesFrontendService.save(variables)
    }

}
