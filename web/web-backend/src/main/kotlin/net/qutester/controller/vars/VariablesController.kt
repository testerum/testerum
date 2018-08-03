package net.qutester.controller.vars

import net.qutester.model.variable.Variable
import net.qutester.service.variables.VariablesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/variables")
open class VariablesController(var variablesService: VariablesService) {

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getVariables(): List<Variable> {
        return variablesService.getVariables()
    }

    @RequestMapping (method = [RequestMethod.POST])
    @ResponseBody
    fun save(@RequestBody variables: List<Variable>): List<Variable> {
        return variablesService.save(variables)
    }
}