package com.testerum.web_backend.services.variables

import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.variable.AllProjectVariables
import com.testerum.model.variable.Variable
import com.testerum.model.variable.VariablesEnvironment
import com.testerum.web_backend.services.project.WebProjectManager

class VariablesFrontendService(private val webProjectManager: WebProjectManager,
                               private val variablesFileService: VariablesFileService) {

    private fun getVariablesDir() = webProjectManager.getProjectServices().dirs().getVariablesDir()

    fun getAllProjectVariables(): AllProjectVariables {
        val projectVariables = variablesFileService.getProjectVariables(getVariablesDir())

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

    fun save(variables: List<Variable>): List<Variable> {
        TODO("not yet implemented")
//        return variablesFileService.save(variables, getVariablesDir())
    }

    fun getVariables(): List<Variable> {
//        return variablesFileService.getVariables(getVariablesDir())
        TODO("not yet implemented")
    }

}
