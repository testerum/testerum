package com.testerum.service.variables

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.file_repository.model.RepositoryFile
import com.testerum.file_repository.model.RepositoryFileChange
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.model.variable.Variable

class VariablesService(val fileRepositoryService: FileRepositoryService,
                       val jsonObjectMapper: ObjectMapper) {

    companion object {
        private val VARIABLES_FILE_PATH: Path = Path.createInstance("variables.json")
    }

    fun save(variables: List<Variable>): List<Variable> {

        val varMap = variables.map { it.key to it.value }.toMap()

        fileRepositoryService.delete(KnownPath(
                VARIABLES_FILE_PATH,
                FileType.VARIABLES
        ))
        fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(
                                        VARIABLES_FILE_PATH,
                                        FileType.VARIABLES
                                ),
                                jsonObjectMapper.writeValueAsString(varMap)
                        )
                )
        )
        return variables
    }

    fun getVariables(): List<Variable> {
        val variablesAsMap = getVariablesAsMap()

        return variablesAsMap.map { Variable(it.key, it.value) }
    }

    fun getVariablesAsMap(): Map<String, String> {
        val variablesRepositoryFile = fileRepositoryService.getByPath(KnownPath(
                VARIABLES_FILE_PATH,
                FileType.VARIABLES
        ))?: return emptyMap()

        val variablesAsString = variablesRepositoryFile.body

        return jsonObjectMapper.readValue(variablesAsString)
    }
}
