package com.testerum.web_backend.services.variables

import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.variable.Variable
import com.testerum.web_backend.services.dirs.FrontendDirs

class VariablesFrontendService(private val frontendDirs: FrontendDirs,
                               private val variablesFileService: VariablesFileService) {

    fun save(variables: List<Variable>): List<Variable> {
        val variablesDir = frontendDirs.getRequiredVariablesDir()

        return variablesFileService.save(variables, variablesDir)
    }

    fun getVariables(): List<Variable> {
        val repositoryDir = frontendDirs.getRepositoryDir()
                ?:  return emptyList()

        val variablesDir = frontendDirs.getVariablesDir(repositoryDir)

        return variablesFileService.getVariables(variablesDir)
    }

}
