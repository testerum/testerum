package com.testerum.web_backend.services.variables

import com.testerum.file_service.file.LocalVariablesFileService
import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.variable.AllProjectVariables
import com.testerum.model.variable.FileLocalVariables
import com.testerum.model.variable.FileProjectLocalVariables
import com.testerum.model.variable.ProjectVariables
import com.testerum.model.variable.ReservedVariableEnvironmentNames
import com.testerum.model.variable.Variable
import com.testerum.model.variable.VariablesEnvironment
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.project.WebProjectManager
import java.util.*

class VariablesFrontendService(private val webProjectManager: WebProjectManager,
                               private val frontendDirs: FrontendDirs,
                               private val variablesFileService: VariablesFileService,
                               private val localVariablesFileService: LocalVariablesFileService) {

    private fun getVariablesDir() = webProjectManager.getProjectServices().dirs().getVariablesDir()
    private fun getFileLocalVariablesFile() = frontendDirs.getFileLocalVariablesFile()

    fun getAllProjectVariables(): AllProjectVariables {
        val projectVariables = variablesFileService.getProjectVariables(getVariablesDir())
        val fileLocalVariables = localVariablesFileService.load(
                getFileLocalVariablesFile()
        )

        val projectId = webProjectManager.getProjectServices().project.id

        return mapProjectVariablesToAllProjectVariables(projectId, projectVariables, fileLocalVariables)
    }

    fun saveAllProjectVariables(allProjectVariables: AllProjectVariables): AllProjectVariables {
        val defaultVariables = mapListOfVarsToMap(allProjectVariables.defaultVariables)
        val environments = mapListOfEnvsToMap(allProjectVariables.environments)

        val projectVariables = ProjectVariables(
                defaultVariables = defaultVariables,
                environments = environments
        )

        val projectId = webProjectManager.getProjectServices().project.id

        val savedProjectVariables = variablesFileService.saveProjectVariables(projectVariables, getVariablesDir())

        val fileLocalVariables = localVariablesFileService.load(
                getFileLocalVariablesFile()
        )

        val projectLocalVariables = TreeMap<String, FileProjectLocalVariables>()
        projectLocalVariables.putAll(fileLocalVariables.projectLocalVariables)
        projectLocalVariables[projectId] = FileProjectLocalVariables(
                currentEnvironment = allProjectVariables.currentEnvironment,
                localVariables = mapListOfVarsToMap(allProjectVariables.localVariables)
        )

        val savedFileLocalVariables = localVariablesFileService.save(
                FileLocalVariables(projectLocalVariables),
                getFileLocalVariablesFile()
        )

        return mapProjectVariablesToAllProjectVariables(projectId, savedProjectVariables, savedFileLocalVariables)
    }

    private fun mapProjectVariablesToAllProjectVariables(projectId: String,
                                                         projectVariables: ProjectVariables,
                                                         fileLocalVariables: FileLocalVariables): AllProjectVariables {
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

        val fileProjectLocalVariables = fileLocalVariables.getFileProjectLocalVariables(projectId)

        val currentEnvironment = fileProjectLocalVariables?.currentEnvironment ?: ReservedVariableEnvironmentNames.DEFAULT
        val localVariables = if (fileProjectLocalVariables != null) {
            fileProjectLocalVariables.localVariables.map {
                Variable(it.key, it.value)
            }
        } else {
            emptyList()
        }

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

    fun saveCurrentEnvironment(currentEnvironment: String): String {
        val projectId = webProjectManager.getProjectServices().project.id

        val fileLocalVariables = localVariablesFileService.load(
                getFileLocalVariablesFile()
        )

        val projectLocalVariables = TreeMap<String, FileProjectLocalVariables>()
        projectLocalVariables.putAll(fileLocalVariables.projectLocalVariables)
        projectLocalVariables[projectId] = FileProjectLocalVariables(
                currentEnvironment = currentEnvironment,
                localVariables = projectLocalVariables[projectId]?.localVariables ?: TreeMap()
        )

        val savedFileLocalVariables = localVariablesFileService.save(
                FileLocalVariables(projectLocalVariables),
                getFileLocalVariablesFile()
        )

        return savedFileLocalVariables.projectLocalVariables[projectId]?.currentEnvironment ?: ReservedVariableEnvironmentNames.DEFAULT
    }

}
