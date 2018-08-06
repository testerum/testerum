package com.testerum.web_backend.controller.vars

import com.testerum.model.variable.Variable
import com.testerum.service.variables.VariablesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/variables")
open class VariablesController(var variablesService: VariablesService) {

    @RequestMapping (method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getVariables(): List<Variable> {
        return variablesService.getVariables()
    }

    @RequestMapping (method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun save(@RequestBody variables: List<Variable>): List<Variable> {
        return variablesService.save(variables)
    }

}