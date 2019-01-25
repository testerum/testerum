package com.testerum.web_backend.services.variables

import com.testerum.file_service.file.VariablesFileService
import com.testerum.model.variable.Variable
import com.testerum.web_backend.services.project.WebProjectManager

class VariablesFrontendService(private val webProjectManager: WebProjectManager,
                               private val variablesFileService: VariablesFileService) {

    private fun getVariablesDir() = webProjectManager.getProjectServices().dirs().getVariablesDir()

    fun save(variables: List<Variable>): List<Variable> {
        return variablesFileService.save(variables, getVariablesDir())
    }

    fun getVariables(): List<Variable> {
        return variablesFileService.getVariables(getVariablesDir())
    }

}
