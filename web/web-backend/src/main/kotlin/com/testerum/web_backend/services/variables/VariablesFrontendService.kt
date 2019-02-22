package com.testerum.web_backend.services.variables

import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.variable.AllProjectVariables
import com.testerum.model.variable.ProjectVariables
import com.testerum.model.variable.Variable
import com.testerum.model.variable.VariablesEnvironment
import com.testerum.web_backend.services.project.WebProjectManager
import java.util.*

class VariablesFrontendService(private val webProjectManager: WebProjectManager,
                               private val variablesFileService: VariablesFileService) {

    private fun getVariablesDir() = webProjectManager.getProjectServices().dirs().getVariablesDir()

    fun getAllProjectVariables(): AllProjectVariables {
        val projectVariables = variablesFileService.getProjectVariables(getVariablesDir())

        return mapProjectVariablesToAllProjectVariables(projectVariables)
    }

    fun saveAllProjectVariables(allProjectVariables: AllProjectVariables): AllProjectVariables {
        val defaultVariables = mapListOfVarsToMap(allProjectVariables.defaultVariables)
        val environments = mapListOfEnvsToMap(allProjectVariables.environments)

        val projectVariables = ProjectVariables(
                defaultVariables = defaultVariables,
                environments = environments
        )

        val savedProjectVariables = variablesFileService.saveProjectVariables(projectVariables, getVariablesDir())

        return mapProjectVariablesToAllProjectVariables(savedProjectVariables)
    }

    private fun mapProjectVariablesToAllProjectVariables(projectVariables: ProjectVariables): AllProjectVariables {
        val defaultVariables = projectVariables.defaultVariables.map {
            Variable(it.key, it.value)
        }

        val environments = ArrayList<VariablesEnvironment>()
        for ((envName, envVars) in projectVariables.environments) {
            environments += VariablesEnvironment(
                    name = envName,
                    variables = envVars.map {
                        Variable(it.key, it.value)
                    }
            )
        }

        val currentEnvironment = VariablesFileService.DEFAULT_ENVIRONMENT_NAME // todo
        val localVariables = emptyList<Variable>() // todo

        return AllProjectVariables(
                currentEnvironment = currentEnvironment,
                localVariables = localVariables,
                defaultVariables = defaultVariables,
                environments = environments
        )
    }

    private fun mapListOfVarsToMap(variables: List<Variable>): TreeMap<String, String> {
        val result = TreeMap<String, String>()

        // if there are duplicate variable keys, the last one will win
        for (variable in variables) {
            result[variable.key] = variable.value
        }

        return result
    }

    private fun mapListOfEnvsToMap(environments: List<VariablesEnvironment>): TreeMap<String, TreeMap<String, String>> {
        val result = TreeMap<String, TreeMap<String, String>>()

        // if there are duplicate variable keys, the last one will win
        for (environment in environments) {
            result[environment.name] = mapListOfVarsToMap(environment.variables)
        }

        return result
    }

}
