package com.testerum.web_backend.controllers.variables

import com.testerum.model.variable.ProjectVariables
import com.testerum.model.variable.Variable
import com.testerum.model.variable.VariablesEnvironment
import com.testerum.web_backend.services.variables.VariablesFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/variables")
class VariablesController(private val variablesFrontendService: VariablesFrontendService) {

    @RequestMapping (method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getProjectVariables(): ProjectVariables {
//        return variablesFrontendService.getVariables()
        return ProjectVariables(
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
    fun save(@RequestBody projectVariables: ProjectVariables): ProjectVariables {
//        return variablesFrontendService.save(variables)
        return projectVariables;
    }

    @RequestMapping (method = [RequestMethod.PUT], path = ["/environment"])
    @ResponseBody
    fun save(@RequestBody currentEnvironment: String): String {
        return currentEnvironment;
    }
}
