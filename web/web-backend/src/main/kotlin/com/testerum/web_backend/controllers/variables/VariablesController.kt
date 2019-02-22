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
    fun getProjectVariables(): AllProjectVariables {
//        return variablesFrontendService.getVariables()
        return AllProjectVariables(
                "staging",
                listOf(
                        Variable("URL", "http://localhost:8080"),
                        Variable("PATH", "/test/v1/rest")
                ),
                listOf(
                        Variable("URL", "http://262.212.212.23"),
                        Variable("PATH", "/local/v1/rest")
                ),
                listOf(
                        VariablesEnvironment("" +
                                "development",
                                listOf(
                                        Variable("URL", "http://developmentTesterum.org"),
                                        Variable("PATH", "/dev/v1/rest")
                                )
                        ),
                        VariablesEnvironment("" +
                                "staging",
                                listOf(
                                        Variable("URL", "http://stagingTesterum.org"),
                                        Variable("PATH", "/staging/v1/rest")
                                )
                        )
                )
        )
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
